package com.unievt.service;

import com.unievt.dto.InscriptionCreateDTO;
import com.unievt.dto.InscriptionResponseDTO;
import com.unievt.entity.Evenement;
import com.unievt.entity.Inscription;
import com.unievt.entity.Utilisateur;
import com.unievt.enums.StatutInscriptionEnum;
import com.unievt.mapper.InscriptionMapper;
import com.unievt.repository.EvenementRepository;
import com.unievt.repository.InscriptionRepository;
import com.unievt.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvenementRepository evenementRepository;
    private final InscriptionMapper inscriptionMapper;

    public InscriptionResponseDTO creerInscription(InscriptionCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le corps de la requête est obligatoire");
        }
        if (dto.getEtudiantId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "etudiantId est obligatoire");
        }
        if (dto.getEvenementId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "evenementId est obligatoire");
        }

        Inscription entity = inscriptionMapper.toEntity(dto);

        Utilisateur etudiant = getUtilisateurOrThrow(dto.getEtudiantId(), "Étudiant introuvable: ");
        Evenement evenement = getEvenementOrThrow(dto.getEvenementId());

        entity.setEtudiant(etudiant);
        entity.setEvenement(evenement);

        if (entity.getDateInscription() == null) {
            entity.setDateInscription(LocalDateTime.now());
        }
        if (entity.getStatut() == null) {
            entity.setStatut(StatutInscriptionEnum.LISTE_ATTENTE);
        }

        Inscription saved = inscriptionRepository.save(entity);
        return inscriptionMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public InscriptionResponseDTO lireInscription(Long id) {
        return inscriptionMapper.toResponseDTO(getInscriptionOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<InscriptionResponseDTO> listerInscriptions() {
        return inscriptionRepository.findAll().stream()
                .map(inscriptionMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscriptionResponseDTO> listerInscriptionsParEvenement(Long evenementId) {
        if (evenementId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "evenementId est obligatoire");
        }
        if (!evenementRepository.existsById(evenementId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evenement introuvable: " + evenementId);
        }
        return inscriptionRepository.findByEvenementId(evenementId).stream()
                .map(inscriptionMapper::toResponseDTO)
                .toList();
    }

    public InscriptionResponseDTO confirmer(Long id) {
        Inscription entity = getInscriptionOrThrow(id);
        requireStatut(entity, StatutInscriptionEnum.LISTE_ATTENTE,
                "Confirmation autorisée uniquement depuis LISTE_ATTENTE");
        entity.setStatut(StatutInscriptionEnum.CONFIRMEE);
        return inscriptionMapper.toResponseDTO(inscriptionRepository.save(entity));
    }

    public InscriptionResponseDTO annuler(Long id) {
        Inscription entity = getInscriptionOrThrow(id);
        entity.setStatut(StatutInscriptionEnum.ANNULEE);
        return inscriptionMapper.toResponseDTO(inscriptionRepository.save(entity));
    }

    public String genererQRCode(Long id) {
        Inscription entity = getInscriptionOrThrow(id);
        String qr = UUID.randomUUID().toString();
        entity.setQrCode(qr);
        inscriptionRepository.save(entity);
        return qr;
    }

    public void supprimerInscription(Long id) {
        if (!inscriptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscription introuvable: " + id);
        }
        inscriptionRepository.deleteById(id);
    }

    private Inscription getInscriptionOrThrow(Long id) {
        return inscriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Inscription introuvable: " + id));
    }

    private Evenement getEvenementOrThrow(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Evenement introuvable: " + id));
    }

    private Utilisateur getUtilisateurOrThrow(Long id, String prefixMessage) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        prefixMessage + id));
    }

    private void requireStatut(Inscription entity, StatutInscriptionEnum expected, String message) {
        StatutInscriptionEnum current = entity.getStatut();
        if (current != expected) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
