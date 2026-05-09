package com.unievt;

import com.unievt.dto.ClubCreateDTO;
import com.unievt.dto.ClubResponseDTO;
import com.unievt.dto.ClubUpdateDTO;
import com.unievt.dto.EtudiantCreateDTO;
import com.unievt.dto.EtudiantResponseDTO;
import com.unievt.dto.EtudiantUpdateDTO;
import com.unievt.dto.IntervenantDTO;
import com.unievt.dto.IntervenantResponseDTO;
import com.unievt.dto.IntervenantUpdateDTO;
import com.unievt.dto.UtilisateurCreateDTO;
import com.unievt.dto.UtilisateurResponseDTO;
import com.unievt.dto.UtilisateurUpdateDTO;
import com.unievt.entity.Evenement;
import com.unievt.entity.EvenementIntervenant;
import com.unievt.entity.EvenementIntervenantId;
import com.unievt.entity.Intervenant;
import com.unievt.entity.Utilisateur;
import com.unievt.enums.CategorieEnum;
import com.unievt.enums.RoleEnum;
import com.unievt.enums.StatutEvenementEnum;
import com.unievt.enums.TypeEvenementEnum;
import com.unievt.enums.VisibiliteEnum;
import com.unievt.repository.EvenementIntervenantRepository;
import com.unievt.repository.EvenementRepository;
import com.unievt.repository.IntervenantRepository;
import com.unievt.repository.UtilisateurRepository;
import com.unievt.service.ClubService;
import com.unievt.service.EtudiantService;
import com.unievt.service.IntervenantService;
import com.unievt.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StrikeModulesIntegrationTests {

    @Autowired private UtilisateurService utilisateurService;
    @Autowired private EtudiantService etudiantService;
    @Autowired private ClubService clubService;
    @Autowired private IntervenantService intervenantService;

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private EvenementRepository evenementRepository;
    @Autowired private IntervenantRepository intervenantRepository;
    @Autowired private EvenementIntervenantRepository evenementIntervenantRepository;

    @Test
    void utilisateurCrudFlowWorks() {
        UtilisateurCreateDTO create = UtilisateurCreateDTO.builder()
                .nom("Strike")
                .prenom("Test")
                .email("strike.test@unievt.ma")
                .motDePasse("secret123")
                .role(RoleEnum.DOYEN) // any genuine permission role; null is also valid
                .build();

        UtilisateurResponseDTO created = utilisateurService.creer(create);
        assertNotNull(created.getId());
        assertEquals("strike.test@unievt.ma", created.getEmail());
        assertTrue(created.getActif());

        // password should be hashed in storage, never echoed back
        Utilisateur stored = utilisateurRepository.findById(created.getId()).orElseThrow();
        assertNotEquals("secret123", stored.getMotDePasse());
        assertTrue(stored.getMotDePasse().startsWith("$2"));

        UtilisateurResponseDTO updated = utilisateurService.modifier(created.getId(),
                UtilisateurUpdateDTO.builder().nom("Strike2").telephone("+212600000000").build());
        assertEquals("Strike2", updated.getNom());
        assertEquals("+212600000000", updated.getTelephone());
        // unchanged fields stay put
        assertEquals("Test", updated.getPrenom());

        UtilisateurResponseDTO deactivated = utilisateurService.desactiver(created.getId());
        assertFalse(deactivated.getActif());
        // idempotent: calling desactiver again still leaves it inactive
        UtilisateurResponseDTO stillDeactivated = utilisateurService.desactiver(created.getId());
        assertFalse(stillDeactivated.getActif());

        UtilisateurResponseDTO reactivated = utilisateurService.activer(created.getId());
        assertTrue(reactivated.getActif());
        // idempotent: calling activer again still leaves it active
        UtilisateurResponseDTO stillActive = utilisateurService.activer(created.getId());
        assertTrue(stillActive.getActif());

        utilisateurService.supprimer(created.getId());
        assertThrows(ResponseStatusException.class,
                () -> utilisateurService.lire(created.getId()));
    }

    @Test
    void utilisateurCreerRejectsDuplicateEmail() {
        utilisateurService.creer(UtilisateurCreateDTO.builder()
                .nom("Dup").prenom("One").email("dup@unievt.ma")
                .motDePasse("p").build()); // role is optional now

        assertThrows(ResponseStatusException.class, () ->
                utilisateurService.creer(UtilisateurCreateDTO.builder()
                        .nom("Dup").prenom("Two").email("dup@unievt.ma")
                        .motDePasse("p").build()));
    }

    @Test
    void etudiantCrudFlowWorks() {
        EtudiantResponseDTO created = etudiantService.creer(EtudiantCreateDTO.builder()
                .nom("Etu").prenom("Diant").email("etu.diant@unievt.ma")
                .motDePasse("pass")
                .filiere("GINF").anneeEtude(3).cin("AB123456")
                .build());

        assertNotNull(created.getId());
        // étudiant has no permission role by default — identity comes from the etudiant table
        assertNull(created.getRole());
        assertEquals("GINF", created.getFiliere());
        assertEquals(3, created.getAnneeEtude());

        EtudiantResponseDTO updated = etudiantService.modifier(created.getId(),
                EtudiantUpdateDTO.builder().anneeEtude(4).build());
        assertEquals(4, updated.getAnneeEtude());
        assertEquals("GINF", updated.getFiliere()); // untouched

        List<EtudiantResponseDTO> all = etudiantService.lister();
        assertTrue(all.stream().anyMatch(e -> e.getId().equals(created.getId())));

        etudiantService.supprimer(created.getId());
        assertThrows(ResponseStatusException.class,
                () -> etudiantService.lire(created.getId()));
    }

    @Test
    void etudiantCreerRejectsDuplicateCin() {
        etudiantService.creer(EtudiantCreateDTO.builder()
                .nom("A").prenom("A").email("a@unievt.ma").motDePasse("p")
                .cin("CIN-1").build());

        assertThrows(ResponseStatusException.class, () ->
                etudiantService.creer(EtudiantCreateDTO.builder()
                        .nom("B").prenom("B").email("b@unievt.ma").motDePasse("p")
                        .cin("CIN-1").build()));
    }

    @Test
    void clubCrudFlowWorks() {
        // Any user can be a club president now — role is irrelevant.
        Utilisateur president = utilisateurRepository.save(Utilisateur.builder()
                .nom("Pres").prenom("Ident").email("pres@unievt.ma").motDePasse("x")
                .actif(true).build());

        ClubResponseDTO created = clubService.creer(ClubCreateDTO.builder()
                .nom("Club Strike")
                .description("Test club")
                .categorie("CULTUREL")
                .presidentId(president.getId())
                .build());
        assertNotNull(created.getId());
        assertEquals(president.getId(), created.getPresidentId());
        assertEquals("Pres", created.getPresidentNom());
        assertTrue(created.getActif());

        ClubResponseDTO updated = clubService.modifier(created.getId(),
                ClubUpdateDTO.builder().nom("Club Renommee").actif(false).build());
        assertEquals("Club Renommee", updated.getNom());
        assertFalse(updated.getActif());
        assertEquals("Test club", updated.getDescription()); // untouched

        clubService.supprimer(created.getId());
        assertThrows(ResponseStatusException.class,
                () -> clubService.lire(created.getId()));
    }

    @Test
    void clubCreerRejectsMissingPresident() {
        assertThrows(ResponseStatusException.class, () ->
                clubService.creer(ClubCreateDTO.builder()
                        .nom("Orphelin").presidentId(999999L).build()));
    }

    @Test
    void clubCanBePresidedByAnyUserIncludingEtudiant() {
        // The whole point of the role-model refactor: an étudiant can chair a club.
        EtudiantResponseDTO etu = etudiantService.creer(EtudiantCreateDTO.builder()
                .nom("Pres").prenom("Etu").email("pres.etu@unievt.ma").motDePasse("p")
                .cin("CIN-PRES").build());

        ClubResponseDTO club = clubService.creer(ClubCreateDTO.builder()
                .nom("Club Étudiant").presidentId(etu.getId()).build());

        assertNotNull(club.getId());
        assertEquals(etu.getId(), club.getPresidentId());
    }

    @Test
    void changerEmailUpdatesAndIsIdempotent() {
        Utilisateur u = utilisateurRepository.save(Utilisateur.builder()
                .nom("Mail").prenom("Hop").email("mail.hop@unievt.ma").motDePasse("p")
                .role(RoleEnum.DOYEN).actif(true).build());

        UtilisateurResponseDTO updated = utilisateurService.changerEmail(u.getId(), "mail.new@unievt.ma");
        assertEquals("mail.new@unievt.ma", updated.getEmail());

        // setting the same email again is a no-op (idempotent), not 409
        UtilisateurResponseDTO again = utilisateurService.changerEmail(u.getId(), "mail.new@unievt.ma");
        assertEquals("mail.new@unievt.ma", again.getEmail());
    }

    @Test
    void changerEmailRejectsDuplicate() {
        utilisateurRepository.save(Utilisateur.builder()
                .nom("Existing").prenom("U").email("taken@unievt.ma").motDePasse("p")
                .role(RoleEnum.DOYEN).actif(true).build());
        Utilisateur other = utilisateurRepository.save(Utilisateur.builder()
                .nom("Other").prenom("U").email("other@unievt.ma").motDePasse("p")
                .role(RoleEnum.DOYEN).actif(true).build());

        assertThrows(ResponseStatusException.class, () ->
                utilisateurService.changerEmail(other.getId(), "taken@unievt.ma"));
    }

    @Test
    void changerRoleSwitchesBetweenPermissionRoles() {
        Utilisateur u = utilisateurRepository.save(Utilisateur.builder()
                .nom("Mut").prenom("Able").email("mut@unievt.ma").motDePasse("p")
                .role(RoleEnum.DOYEN).actif(true).build());

        UtilisateurResponseDTO promoted = utilisateurService.changerRole(u.getId(), RoleEnum.ADMIN);
        assertEquals(RoleEnum.ADMIN, promoted.getRole());

        // idempotent: calling with the same role again is fine
        UtilisateurResponseDTO again = utilisateurService.changerRole(u.getId(), RoleEnum.ADMIN);
        assertEquals(RoleEnum.ADMIN, again.getRole());
    }

    @Test
    void changerRoleCanGrantPermissionToEtudiant() {
        // An étudiant who, e.g., joins the events committee, can pick up DOYEN powers
        // without losing their étudiant data row.
        EtudiantResponseDTO etu = etudiantService.creer(EtudiantCreateDTO.builder()
                .nom("Was").prenom("Etu").email("was.etu@unievt.ma").motDePasse("p")
                .cin("CIN-WAS").build());

        UtilisateurResponseDTO promoted = utilisateurService.changerRole(etu.getId(), RoleEnum.DOYEN);
        assertEquals(RoleEnum.DOYEN, promoted.getRole());

        // Étudiant data row still exists alongside the new permission role
        assertNotNull(etudiantService.lire(etu.getId()).getCin());
    }

    @Test
    void intervenantCrudAndListByEvenementWorks() {
        IntervenantResponseDTO i1 = intervenantService.creer(
                IntervenantDTO.builder().nom("Speaker One").institution("UM6P").build());
        IntervenantResponseDTO i2 = intervenantService.creer(
                IntervenantDTO.builder().nom("Speaker Two").institution("EMSI").build());

        assertEquals(2, intervenantService.lister().size());

        IntervenantResponseDTO updated = intervenantService.modifier(i1.getId(),
                IntervenantUpdateDTO.builder().biographie("Bio mise a jour").build());
        assertEquals("Bio mise a jour", updated.getBiographie());
        assertEquals("Speaker One", updated.getNom()); // untouched

        // wire one intervenant to an event
        Utilisateur orga = utilisateurRepository.save(Utilisateur.builder()
                .nom("Org").prenom("A").email("org@unievt.ma").motDePasse("p")
                .role(RoleEnum.RESPONSABLE_EVENEMENTS).actif(true).build());

        Evenement evt = evenementRepository.save(Evenement.builder()
                .titre("Conf X").description("d")
                .categorie(CategorieEnum.CONFERENCE)
                .dateDebut(LocalDateTime.now().plusDays(1))
                .dateFin(LocalDateTime.now().plusDays(1).plusHours(2))
                .capacite(50)
                .statut(StatutEvenementEnum.APPROUVE)
                .visibilite(VisibiliteEnum.UNIVERSITE)
                .type(TypeEvenementEnum.CLUB)
                .organisateur(orga)
                .build());

        Intervenant managedI1 = intervenantRepository.findById(i1.getId()).orElseThrow();
        evenementIntervenantRepository.save(EvenementIntervenant.builder()
                .id(new EvenementIntervenantId(evt.getId(), i1.getId()))
                .evenement(evt)
                .intervenant(managedI1)
                .build());

        List<IntervenantResponseDTO> forEvt = intervenantService.listerParEvenement(evt.getId());
        assertEquals(1, forEvt.size());
        assertEquals(i1.getId(), forEvt.get(0).getId());

        // delete should also clear join rows
        intervenantService.supprimer(i1.getId());
        assertTrue(intervenantService.listerParEvenement(evt.getId()).isEmpty());
        assertEquals(1, intervenantService.lister().size());
        assertEquals(i2.getId(), intervenantService.lister().get(0).getId());
    }
}
