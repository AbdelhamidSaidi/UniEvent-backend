package com.unievt.controller;

import com.unievt.dto.EtudiantCreateDTO;
import com.unievt.dto.EtudiantResponseDTO;
import com.unievt.dto.EtudiantUpdateDTO;
import com.unievt.service.EtudiantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;

    // POST /etudiants — créer un étudiant
    @PostMapping
    public ResponseEntity<EtudiantResponseDTO> creer(@Valid @RequestBody EtudiantCreateDTO dto) {
        EtudiantResponseDTO created = etudiantService.creer(dto);
        return ResponseEntity
                .created(URI.create("/etudiants/" + created.getId()))
                .body(created);
    }

    // GET /etudiants — lister tous les étudiants
    @GetMapping
    public List<EtudiantResponseDTO> lister() {
        return etudiantService.lister();
    }

    // GET /etudiants/{id} — lire un étudiant
    @GetMapping("/{id}")
    public EtudiantResponseDTO lire(@PathVariable Long id) {
        return etudiantService.lire(id);
    }

    // PUT /etudiants/{id} — modifier un étudiant
    @PutMapping("/{id}")
    public EtudiantResponseDTO modifier(@PathVariable Long id,
                                        @Valid @RequestBody EtudiantUpdateDTO dto) {
        return etudiantService.modifier(id, dto);
    }

    // DELETE /etudiants/{id} — supprimer un étudiant
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        etudiantService.supprimer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
