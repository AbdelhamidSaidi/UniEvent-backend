package com.unievt.service;

import com.unievt.dto.EvenementCreateDTO;
import com.unievt.dto.EvenementResponseDTO;
import com.unievt.dto.EvenementUpdateDTO;
import com.unievt.entity.Club;
import com.unievt.entity.Evenement;
import com.unievt.entity.Utilisateur;
import com.unievt.enums.StatutEvenementEnum;
import com.unievt.mapper.EvenementMapper;
import com.unievt.repository.ClubRepository;
import com.unievt.repository.EvenementRepository;
import com.unievt.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvenementService {

    private static final Logger log = LoggerFactory.getLogger(EvenementService.class);

    private final EvenementRepository evenementRepository;
    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvenementMapper evenementMapper;

    public EvenementResponseDTO creerEvenement(EvenementCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le corps de la requête est obligatoire");
        }
        validateDatesAndCapacite(dto);

        Evenement entity = evenementMapper.toEntity(dto);

        if (dto.getClubId() != null) {
            entity.setClub(getClubOrThrow(dto.getClubId()));
        } else {
            entity.setClub(null);
        }

        if (dto.getOrganisateurId() != null) {
            entity.setOrganisateur(getUtilisateurOrThrow(dto.getOrganisateurId(), "Organisateur introuvable: ")); 
        } else {
            entity.setOrganisateur(null);
        }

        if (entity.getStatut() == null) {
            entity.setStatut(StatutEvenementEnum.BROUILLON);
        }

        Evenement saved = evenementRepository.save(entity);
        return evenementMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public EvenementResponseDTO lireEvenement(Long id) {
        return evenementMapper.toResponseDTO(getEvenementOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<EvenementResponseDTO> listerEvenements() {
        return evenementRepository.findAll().stream()
                .map(evenementMapper::toResponseDTO)
                .toList();
    }

    public EvenementResponseDTO modifierEvenement(Long id, EvenementUpdateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le corps de la requête est obligatoire");
        }
        Evenement entity = getEvenementOrThrow(id);
        evenementMapper.updateEntityFromDTO(dto, entity);
        return evenementMapper.toResponseDTO(evenementRepository.save(entity));
    }

    public void supprimerEvenement(Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evenement introuvable: " + id);
        }
        evenementRepository.deleteById(id);
    }

    public EvenementResponseDTO soumettre(Long id) {
        Evenement entity = getEvenementOrThrow(id);
        requireStatut(entity, StatutEvenementEnum.BROUILLON, "Soumission autorisée uniquement depuis BROUILLON");
        entity.setStatut(StatutEvenementEnum.SOUMIS);
        return evenementMapper.toResponseDTO(evenementRepository.save(entity));
    }

    public EvenementResponseDTO verifier(Long id) {
        Evenement entity = getEvenementOrThrow(id);
        requireStatut(entity, StatutEvenementEnum.SOUMIS, "Vérification autorisée uniquement depuis SOUMIS");
        entity.setStatut(StatutEvenementEnum.VERIFIE);
        return evenementMapper.toResponseDTO(evenementRepository.save(entity));
    }

    public EvenementResponseDTO approuver(Long id) {
        Evenement entity = getEvenementOrThrow(id);
        StatutEvenementEnum current = safeStatut(entity);
        if (current != StatutEvenementEnum.VERIFIE && current != StatutEvenementEnum.RESERVATION_EN_ATTENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Approbation autorisée uniquement depuis VERIFIE ou RESERVATION_EN_ATTENTE");
        }
        entity.setStatut(StatutEvenementEnum.APPROUVE);
        return evenementMapper.toResponseDTO(evenementRepository.save(entity));
    }

    public EvenementResponseDTO rejeter(Long id, String motif) {
        if (motif == null || motif.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le motif de rejet est obligatoire");
        }
        Evenement entity = getEvenementOrThrow(id);
        entity.setStatut(StatutEvenementEnum.REJETE);
        log.info("Evenement {} rejeté. Motif: {}", id, motif);
        return evenementMapper.toResponseDTO(evenementRepository.save(entity));
    }

    public EvenementResponseDTO annuler(Long id) {
        Evenement entity = getEvenementOrThrow(id);
        entity.setStatut(StatutEvenementEnum.ANNULE);
        return evenementMapper.toResponseDTO(evenementRepository.save(entity));
    }

    public EvenementResponseDTO archiver(Long id) {
        Evenement entity = getEvenementOrThrow(id);
        entity.setStatut(StatutEvenementEnum.TERMINE);
        return evenementMapper.toResponseDTO(evenementRepository.save(entity));
    }

    private Evenement getEvenementOrThrow(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Evenement introuvable: " + id));
    }

    private Club getClubOrThrow(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Club introuvable: " + id));
    }

    private Utilisateur getUtilisateurOrThrow(Long id, String prefixMessage) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        prefixMessage + id));
    }

    private void requireStatut(Evenement entity, StatutEvenementEnum expected, String message) {
        StatutEvenementEnum current = safeStatut(entity);
        if (current != expected) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private StatutEvenementEnum safeStatut(Evenement entity) {
        return entity.getStatut() != null ? entity.getStatut() : StatutEvenementEnum.BROUILLON;
    }

    private void validateDatesAndCapacite(EvenementCreateDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La date de fin doit être postérieure à la date de début");
        }
        if (dto.getCapacite() != null && dto.getCapacite() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La capacité doit être un entier positif");
        }
    }
}
