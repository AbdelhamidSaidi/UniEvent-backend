package com.unievt.service;

import com.unievt.dto.ClubCreateDTO;
import com.unievt.dto.ClubResponseDTO;
import com.unievt.dto.ClubUpdateDTO;
import com.unievt.entity.Club;
import com.unievt.entity.Utilisateur;
import com.unievt.mapper.ClubMapper;
import com.unievt.repository.ClubRepository;
import com.unievt.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClubService {

    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClubMapper clubMapper;

    public ClubResponseDTO creer(ClubCreateDTO dto) {
        // Any user can be a club president. The Club.president FK is the source of truth;
        // RoleEnum is for permission grants and intentionally does NOT include "PRESIDENT_CLUB".
        Utilisateur president = utilisateurRepository.findById(dto.getPresidentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Président introuvable: " + dto.getPresidentId()));
        Club entity = clubMapper.toEntity(dto);
        entity.setPresident(president);
        if (entity.getActif() == null) {
            entity.setActif(true);
        }
        return clubMapper.toResponseDTO(clubRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ClubResponseDTO> lister() {
        return clubRepository.findAll().stream()
                .map(clubMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClubResponseDTO lire(Long id) {
        return clubMapper.toResponseDTO(getOrThrow(id));
    }

    public ClubResponseDTO modifier(Long id, ClubUpdateDTO dto) {
        Club entity = getOrThrow(id);
        clubMapper.updateEntityFromDTO(dto, entity);
        return clubMapper.toResponseDTO(clubRepository.save(entity));
    }

    public void supprimer(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Club introuvable: " + id);
        }
        clubRepository.deleteById(id);
    }

    private Club getOrThrow(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Club introuvable: " + id));
    }
}
