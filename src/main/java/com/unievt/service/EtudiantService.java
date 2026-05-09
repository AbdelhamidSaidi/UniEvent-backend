package com.unievt.service;

import com.unievt.dto.EtudiantCreateDTO;
import com.unievt.dto.EtudiantResponseDTO;
import com.unievt.dto.EtudiantUpdateDTO;
import com.unievt.entity.Etudiant;
import com.unievt.mapper.EtudiantMapper;
import com.unievt.repository.EtudiantRepository;
import com.unievt.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantMapper etudiantMapper;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EtudiantResponseDTO creer(EtudiantCreateDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un utilisateur avec cet email existe déjà");
        }
        if (dto.getCin() != null && etudiantRepository.findByCin(dto.getCin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un étudiant avec ce CIN existe déjà");
        }
        Etudiant entity = etudiantMapper.toEntity(dto);
        entity.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        // No role assigned by default. Étudiant identity is captured by the
        // etudiant table itself; permission roles are granted explicitly via
        // PATCH /utilisateurs/{id}/role only when needed.
        if (entity.getActif() == null) {
            entity.setActif(true);
        }
        return etudiantMapper.toResponseDTO(etudiantRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<EtudiantResponseDTO> lister() {
        return etudiantRepository.findAll().stream()
                .map(etudiantMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EtudiantResponseDTO lire(Long id) {
        return etudiantMapper.toResponseDTO(getOrThrow(id));
    }

    public EtudiantResponseDTO modifier(Long id, EtudiantUpdateDTO dto) {
        Etudiant entity = getOrThrow(id);
        etudiantMapper.updateEntityFromDTO(dto, entity);
        return etudiantMapper.toResponseDTO(etudiantRepository.save(entity));
    }

    public void supprimer(Long id) {
        if (!etudiantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Étudiant introuvable: " + id);
        }
        etudiantRepository.deleteById(id);
    }

    private Etudiant getOrThrow(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Étudiant introuvable: " + id));
    }
}
