package com.unievt;

import com.unievt.entity.Club;
import com.unievt.repository.ClubRepository;
import com.unievt.enums.*;
import com.unievt.entity.Evenement;
import com.unievt.entity.EvenementIntervenant;
import com.unievt.entity.EvenementIntervenantId;
import com.unievt.entity.Intervenant;
import com.unievt.repository.EvenementIntervenantRepository;
import com.unievt.repository.EvenementRepository;
import com.unievt.repository.IntervenantRepository;
import com.unievt.entity.Inscription;
import com.unievt.repository.InscriptionRepository;
import com.unievt.entity.Etudiant;
import com.unievt.entity.Utilisateur;
import com.unievt.repository.EtudiantRepository;
import com.unievt.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UniEventBackendApplicationTests {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private ClubRepository clubRepository;
    @Autowired private IntervenantRepository intervenantRepository;
    @Autowired private EvenementRepository evenementRepository;
    @Autowired private InscriptionRepository inscriptionRepository;
    @Autowired private EvenementIntervenantRepository evenementIntervenantRepository;

    // ─── shared fixtures ────────────────────────────────────────────
    private Utilisateur doyen;
    private Utilisateur president;
    private Utilisateur responsable;
    private Etudiant etudiant1;
    private Etudiant etudiant2;
    private Club club;
    private Intervenant intervenant1;
    private Intervenant intervenant2;
    private Evenement evenement;

    @BeforeEach
    void setUp() {
        // Users
        doyen = utilisateurRepository.save(Utilisateur.builder()
                .nom("Alami").prenom("Hassan")
                .email("doyen@unievt.ma").motDePasse("pass")
                .role(RoleEnum.DOYEN).actif(true).build());

        // "president" here is the club's président — modeled by the Club.president FK,
        // not a permission role. Role is therefore null for this user.
        president = utilisateurRepository.save(Utilisateur.builder()
                .nom("Hassoub").prenom("Strike")
                .email("strike@unievt.ma").motDePasse("pass")
                .actif(true).build());

        responsable = utilisateurRepository.save(Utilisateur.builder()
                .nom("Cherkaoui").prenom("Nadia")
                .email("nadia@unievt.ma").motDePasse("pass")
                .role(RoleEnum.RESPONSABLE_EVENEMENTS).actif(true).build());

        // Students — étudiant identity comes from the etudiant table itself,
        // so no permission role is set.
        etudiant1 = etudiantRepository.save(Etudiant.builder()
                .nom("Alaoui").prenom("Fatima")
                .email("fatima@unievt.ma").motDePasse("pass")
                .actif(true)
                .filiere("Génie Informatique").anneeEtude(2).cin("AB111111").build());

        etudiant2 = etudiantRepository.save(Etudiant.builder()
                .nom("Ziani").prenom("Mehdi")
                .email("mehdi@unievt.ma").motDePasse("pass")
                .actif(true)
                .filiere("Génie Civil").anneeEtude(3).cin("CD222222").build());

        // Club
        club = clubRepository.save(Club.builder()
                .nom("Club IA").description("Intelligence Artificielle")
                .categorie("Technologie").actif(true)
                .president(president).build());

        // Intervenants
        intervenant1 = intervenantRepository.save(Intervenant.builder()
                .nom("Dupont").institution("MIT")
                .biographie("Expert en IA").build());

        intervenant2 = intervenantRepository.save(Intervenant.builder()
                .nom("Garcia").institution("Stanford")
                .biographie("Chercheur en NLP").build());

        // Evenement
        evenement = evenementRepository.save(Evenement.builder()
                .titre("Conférence IA 2025")
                .description("Une conférence sur l'IA")
                .categorie(CategorieEnum.CONFERENCE)
                .dateDebut(LocalDateTime.now().plusDays(7))
                .dateFin(LocalDateTime.now().plusDays(7).plusHours(3))
                .capacite(100)
                .statut(StatutEvenementEnum.APPROUVE)
                .visibilite(VisibiliteEnum.UNIVERSITE)
                .type(TypeEvenementEnum.CLUB)
                .club(club)
                .organisateur(president).build());
    }

    // ─────────────────────────────────────────────
    //  UTILISATEUR
    // ─────────────────────────────────────────────

    @Test
    void testAllRolesSaved() {
        assertEquals(RoleEnum.DOYEN, doyen.getRole());
        // "president" here is identified by the Club.president FK, not by a role.
        assertNull(president.getRole());
        assertEquals(RoleEnum.RESPONSABLE_EVENEMENTS, responsable.getRole());
        System.out.println(" All roles saved correctly");
    }

    @Test
    void testFindByEmail() {
        Optional<Utilisateur> found = utilisateurRepository.findByEmail("doyen@unievt.ma");
        assertTrue(found.isPresent());
        assertEquals("Alami", found.get().getNom());
        System.out.println(" findByEmail works");
    }

    @Test
    void testExistsByEmail() {
        assertTrue(utilisateurRepository.existsByEmail("strike@unievt.ma"));
        assertFalse(utilisateurRepository.existsByEmail("nobody@unievt.ma"));
        System.out.println(" existsByEmail works");
    }

    @Test
    void testDateCreationAutoSet() {
        assertNotNull(doyen.getDateCreation());
        System.out.println(" dateCreation auto-set: " + doyen.getDateCreation());
    }

    @Test
    void testInactiveUser() {
        Utilisateur inactif = utilisateurRepository.save(Utilisateur.builder()
                .nom("Test").prenom("Inactif")
                .email("inactif@unievt.ma").motDePasse("pass")
                .actif(false).build()); // role nullable; not relevant for this test
        assertFalse(inactif.getActif());
        System.out.println(" Inactive user saved correctly");
    }

    // ─────────────────────────────────────────────
    //  ETUDIANT
    // ─────────────────────────────────────────────

    @Test
    void testEtudiantFields() {
        assertEquals("Génie Informatique", etudiant1.getFiliere());
        assertEquals(2, etudiant1.getAnneeEtude());
        assertEquals("AB111111", etudiant1.getCin());
        System.out.println(" Etudiant fields correct");
    }

    @Test
    void testFindByCin() {
        Optional<Etudiant> found = etudiantRepository.findByCin("AB111111");
        assertTrue(found.isPresent());
        assertEquals("Alaoui", found.get().getNom());
        System.out.println(" findByCin works");
    }

    @Test
    void testEtudiantIsAlsoUtilisateur() {
        Optional<Utilisateur> found = utilisateurRepository.findByEmail("fatima@unievt.ma");
        assertTrue(found.isPresent());
        assertInstanceOf(Etudiant.class, found.get());
        System.out.println(" Etudiant is also Utilisateur — inheritance works");
    }

    @Test
    void testMultipleEtudiants() {
        assertEquals(2, etudiantRepository.findAll().size());
        System.out.println(" Both etudiants saved");
    }

    // ─────────────────────────────────────────────
    //  CLUB
    // ─────────────────────────────────────────────

    @Test
    void testClubSaved() {
        assertNotNull(club.getId());
        assertEquals("Club IA", club.getNom());
        System.out.println(" Club saved: " + club.getId());
    }

    @Test
    void testClubPresidentRelationship() {
        assertNotNull(club.getPresident());
        assertEquals("Hassoub", club.getPresident().getNom());
        System.out.println(" Club president relationship works");
    }

    @Test
    void testFindActiveClubs() {
        clubRepository.save(Club.builder().nom("Club Inactif")
                .categorie("Sport").actif(false).president(president).build());
        List<Club> active = clubRepository.findByActifTrue();
        assertEquals(1, active.size());
        System.out.println(" findByActifTrue works, found: " + active.size());
    }

    @Test
    void testFindClubsByCategorie() {
        clubRepository.save(Club.builder().nom("Club Web")
                .categorie("Technologie").actif(true).president(president).build());
        clubRepository.save(Club.builder().nom("Club Foot")
                .categorie("Sport").actif(true).president(president).build());

        List<Club> techClubs = clubRepository.findByCategorie("Technologie");
        assertEquals(2, techClubs.size());
        System.out.println(" findByCategorie works, found: " + techClubs.size());
    }

    @Test
    void testMultipleClubsSamePresident() {
        clubRepository.save(Club.builder().nom("Club Robotique")
                .categorie("Technologie").actif(true).president(president).build());
        List<Club> all = clubRepository.findAll();
        assertEquals(2, all.size());
        System.out.println(" President can manage multiple clubs");
    }

    // ─────────────────────────────────────────────
    //  INTERVENANT
    // ─────────────────────────────────────────────

    @Test
    void testIntervenantSaved() {
        assertNotNull(intervenant1.getId());
        assertEquals("MIT", intervenant1.getInstitution());
        System.out.println(" Intervenant saved: " + intervenant1.getId());
    }

    @Test
    void testMultipleIntervenants() {
        assertEquals(2, intervenantRepository.findAll().size());
        System.out.println(" Both intervenants saved");
    }

    @Test
    void testUpdateIntervenant() {
        intervenant1.setNom("Dupont Updated");
        intervenant1.setInstitution("Harvard");
        Intervenant updated = intervenantRepository.save(intervenant1);
        assertEquals("Harvard", updated.getInstitution());
        System.out.println(" Intervenant updated successfully");
    }

    @Test
    void testDeleteIntervenant() {
        Long id = intervenant2.getId();
        intervenantRepository.deleteById(id);
        assertFalse(intervenantRepository.existsById(id));
        System.out.println(" Intervenant deleted successfully");
    }

    // ─────────────────────────────────────────────
    //  EVENEMENT
    // ─────────────────────────────────────────────

    @Test
    void testEvenementSaved() {
        assertNotNull(evenement.getId());
        assertEquals("Conférence IA 2025", evenement.getTitre());
        assertEquals(StatutEvenementEnum.APPROUVE, evenement.getStatut());
        System.out.println(" Evenement saved: " + evenement.getId());
    }

    @Test
    void testEvenementClubRelationship() {
        assertNotNull(evenement.getClub());
        assertEquals("Club IA", evenement.getClub().getNom());
        System.out.println(" Evenement club relationship works");
    }

    @Test
    void testEvenementOrganisateurRelationship() {
        assertNotNull(evenement.getOrganisateur());
        assertEquals("Hassoub", evenement.getOrganisateur().getNom());
        System.out.println(" Evenement organisateur relationship works");
    }

    @Test
    void testFindEvenementByStatut() {
        evenementRepository.save(Evenement.builder()
                .titre("Atelier Python").categorie(CategorieEnum.ATELIER)
                .dateDebut(LocalDateTime.now().plusDays(14))
                .dateFin(LocalDateTime.now().plusDays(14).plusHours(2))
                .capacite(30).statut(StatutEvenementEnum.SOUMIS)
                .visibilite(VisibiliteEnum.UNIVERSITE)
                .type(TypeEvenementEnum.INSTITUTIONNEL)
                .organisateur(responsable).build());

        List<Evenement> approuves = evenementRepository.findByStatut(StatutEvenementEnum.APPROUVE);
        List<Evenement> soumis = evenementRepository.findByStatut(StatutEvenementEnum.SOUMIS);
        assertEquals(1, approuves.size());
        assertEquals(1, soumis.size());
        System.out.println(" findByStatut works");
    }

    @Test
    void testFindEvenementByClub() {
        List<Evenement> events = evenementRepository.findByClubId(club.getId());
        assertEquals(1, events.size());
        System.out.println(" findByClubId works");
    }

    @Test
    void testFindEvenementByOrganisateur() {
        List<Evenement> events = evenementRepository.findByOrganisateurId(president.getId());
        assertEquals(1, events.size());
        System.out.println(" findByOrganisateurId works");
    }

    @Test
    void testFindEvenementByCategorie() {
        List<Evenement> conferences = evenementRepository.findByCategorie(CategorieEnum.CONFERENCE);
        assertEquals(1, conferences.size());
        System.out.println(" findByCategorie works");
    }

    @Test
    void testMultipleEvenements() {
        evenementRepository.save(Evenement.builder()
                .titre("Hackathon 2025").categorie(CategorieEnum.COMPETITION)
                .dateDebut(LocalDateTime.now().plusDays(30))
                .dateFin(LocalDateTime.now().plusDays(31))
                .capacite(50).statut(StatutEvenementEnum.APPROUVE)
                .visibilite(VisibiliteEnum.PUBLIC)
                .type(TypeEvenementEnum.INSTITUTIONNEL)
                .club(club).organisateur(responsable).build());

        assertEquals(2, evenementRepository.findAll().size());
        System.out.println(" Multiple evenements saved");
    }

    // ─────────────────────────────────────────────
    //  INSCRIPTION
    // ─────────────────────────────────────────────

    @Test
    void testInscriptionSaved() {
        Inscription i = inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-001").present(false)
                .etudiant(etudiant1).evenement(evenement).build());

        assertNotNull(i.getId());
        assertEquals(StatutInscriptionEnum.CONFIRMEE, i.getStatut());
        System.out.println(" Inscription saved: " + i.getId());
    }

    @Test
    void testMultipleInscriptions() {
        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-001").present(false)
                .etudiant(etudiant1).evenement(evenement).build());

        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.LISTE_ATTENTE)
                .qrCode("QR-002").present(false)
                .etudiant(etudiant2).evenement(evenement).build());

        assertEquals(2, inscriptionRepository.findAll().size());
        System.out.println(" Multiple inscriptions saved");
    }

    @Test
    void testFindInscriptionsByEvenement() {
        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-001").present(false)
                .etudiant(etudiant1).evenement(evenement).build());

        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-002").present(true)
                .etudiant(etudiant2).evenement(evenement).build());

        List<Inscription> found = inscriptionRepository.findByEvenementId(evenement.getId());
        assertEquals(2, found.size());
        System.out.println(" findByEvenementId works");
    }

    @Test
    void testFindInscriptionsByEtudiant() {
        Evenement evt2 = evenementRepository.save(Evenement.builder()
                .titre("Atelier Design").categorie(CategorieEnum.ATELIER)
                .dateDebut(LocalDateTime.now().plusDays(10))
                .dateFin(LocalDateTime.now().plusDays(10).plusHours(2))
                .capacite(20).statut(StatutEvenementEnum.APPROUVE)
                .visibilite(VisibiliteEnum.UNIVERSITE)
                .type(TypeEvenementEnum.CLUB)
                .club(club).organisateur(president).build());

        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-001").present(false)
                .etudiant(etudiant1).evenement(evenement).build());

        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-002").present(false)
                .etudiant(etudiant1).evenement(evt2).build());

        List<Inscription> found = inscriptionRepository.findByEtudiantId(etudiant1.getId());
        assertEquals(2, found.size());
        System.out.println(" findByEtudiantId works — etudiant inscribed in 2 events");
    }

    @Test
    void testFindInscriptionsByEvenementAndStatut() {
        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-001").present(false)
                .etudiant(etudiant1).evenement(evenement).build());

        inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.LISTE_ATTENTE)
                .qrCode("QR-002").present(false)
                .etudiant(etudiant2).evenement(evenement).build());

        List<Inscription> confirmes = inscriptionRepository
                .findByEvenementIdAndStatut(evenement.getId(), StatutInscriptionEnum.CONFIRMEE);
        assertEquals(1, confirmes.size());
        System.out.println(" findByEvenementIdAndStatut works");
    }

    @Test
    void testInscriptionPresenceUpdate() {
        Inscription i = inscriptionRepository.save(Inscription.builder()
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscriptionEnum.CONFIRMEE)
                .qrCode("QR-001").present(false)
                .etudiant(etudiant1).evenement(evenement).build());

        i.setPresent(true);
        Inscription updated = inscriptionRepository.save(i);
        assertTrue(updated.getPresent());
        System.out.println(" Inscription presence updated successfully");
    }

    // ─────────────────────────────────────────────
    //  EVENEMENT INTERVENANT
    // ─────────────────────────────────────────────

    @Test
    void testEvenementIntervenantSaved() {
        EvenementIntervenantId id = new EvenementIntervenantId(evenement.getId(), intervenant1.getId());
        EvenementIntervenant ei = evenementIntervenantRepository.save(
                new EvenementIntervenant(id, evenement, intervenant1));

        assertNotNull(ei.getId());
        assertEquals(evenement.getId(), ei.getId().getEvenementId());
        System.out.println(" EvenementIntervenant saved with composite PK");
    }

    @Test
    void testMultipleIntervenantsForEvenement() {
        evenementIntervenantRepository.save(new EvenementIntervenant(
                new EvenementIntervenantId(evenement.getId(), intervenant1.getId()),
                evenement, intervenant1));

        evenementIntervenantRepository.save(new EvenementIntervenant(
                new EvenementIntervenantId(evenement.getId(), intervenant2.getId()),
                evenement, intervenant2));

        List<EvenementIntervenant> found = evenementIntervenantRepository
                .findByEvenementId(evenement.getId());
        assertEquals(2, found.size());
        System.out.println(" Multiple intervenants linked to same evenement");
    }

    @Test
    void testFindByIntervenantId() {
        Evenement evt2 = evenementRepository.save(Evenement.builder()
                .titre("Séminaire ML").categorie(CategorieEnum.CONFERENCE)
                .dateDebut(LocalDateTime.now().plusDays(20))
                .dateFin(LocalDateTime.now().plusDays(20).plusHours(2))
                .capacite(80).statut(StatutEvenementEnum.APPROUVE)
                .visibilite(VisibiliteEnum.PUBLIC)
                .type(TypeEvenementEnum.INSTITUTIONNEL)
                .organisateur(responsable).build());

        evenementIntervenantRepository.save(new EvenementIntervenant(
                new EvenementIntervenantId(evenement.getId(), intervenant1.getId()),
                evenement, intervenant1));

        evenementIntervenantRepository.save(new EvenementIntervenant(
                new EvenementIntervenantId(evt2.getId(), intervenant1.getId()),
                evt2, intervenant1));

        List<EvenementIntervenant> found = evenementIntervenantRepository
                .findByIntervenantId(intervenant1.getId());
        assertEquals(2, found.size());
        System.out.println(" findByIntervenantId works — intervenant in 2 events");
    }

    @Test
    void testDeleteEvenementIntervenant() {
        EvenementIntervenantId id = new EvenementIntervenantId(evenement.getId(), intervenant1.getId());
        evenementIntervenantRepository.save(new EvenementIntervenant(id, evenement, intervenant1));

        evenementIntervenantRepository.deleteById(id);
        assertFalse(evenementIntervenantRepository.existsById(id));
        System.out.println(" EvenementIntervenant deleted by composite PK");
    }
}