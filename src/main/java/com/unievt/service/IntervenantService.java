package com.unievt.service;

import com.unievt.dto.IntervenantDTO;
import com.unievt.dto.IntervenantResponseDTO;
import com.unievt.dto.IntervenantUpdateDTO;
import com.unievt.entity.Intervenant;
import com.unievt.mapper.IntervenantMapper;
import com.unievt.repository.EvenementIntervenantRepository;
import com.unievt.repository.IntervenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IntervenantService {

    private final IntervenantRepository intervenantRepository;
    private final EvenementIntervenantRepository evenementIntervenantRepository;
    private final IntervenantMapper intervenantMapper;

    public IntervenantResponseDTO creer(IntervenantDTO dto) {
        Intervenant entity = intervenantMapper.toEntity(dto);
        return intervenantMapper.toResponseDTO(intervenantRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<IntervenantResponseDTO> lister() {
        return intervenantRepository.findAll().stream()
                .map(intervenantMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public IntervenantResponseDTO lire(Long id) {
        return intervenantMapper.toResponseDTO(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<IntervenantResponseDTO> listerParEvenement(Long evenementId) {
        return evenementIntervenantRepository.findByEvenementId(evenementId).stream()
                .map(link -> intervenantMapper.toResponseDTO(link.getIntervenant()))
                .toList();
    }

    public IntervenantResponseDTO modifier(Long id, IntervenantUpdateDTO dto) {
        Intervenant entity = getOrThrow(id);
        intervenantMapper.updateEntityFromDTO(dto, entity);
        return intervenantMapper.toResponseDTO(intervenantRepository.save(entity));
    }

    public void supprimer(Long id) {
        if (!intervenantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Intervenant introuvable: " + id);
        }
        // remove join-table rows first to keep referential integrity
        evenementIntervenantRepository.findByIntervenantId(id)
                .forEach(evenementIntervenantRepository::delete);
        intervenantRepository.deleteById(id);
    }

    private Intervenant getOrThrow(Long id) {
        return intervenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Intervenant introuvable: " + id));
    }
}
