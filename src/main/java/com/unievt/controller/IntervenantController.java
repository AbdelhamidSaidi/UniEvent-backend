package com.unievt.controller;

import com.unievt.dto.IntervenantDTO;
import com.unievt.dto.IntervenantResponseDTO;
import com.unievt.dto.IntervenantUpdateDTO;
import com.unievt.service.IntervenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class IntervenantController {

    private final IntervenantService intervenantService;

    // POST /intervenants — créer un intervenant
    @PostMapping("/intervenants")
    public ResponseEntity<IntervenantResponseDTO> creer(@Valid @RequestBody IntervenantDTO dto) {
        IntervenantResponseDTO created = intervenantService.creer(dto);
        return ResponseEntity
                .created(URI.create("/intervenants/" + created.getId()))
                .body(created);
    }

    // GET /intervenants — lister tous les intervenants
    @GetMapping("/intervenants")
    public List<IntervenantResponseDTO> lister() {
        return intervenantService.lister();
    }

    // GET /intervenants/{id} — lire un intervenant
    @GetMapping("/intervenants/{id}")
    public IntervenantResponseDTO lire(@PathVariable Long id) {
        return intervenantService.lire(id);
    }

    // GET /evenements/{id}/intervenants — lister les intervenants d'un événement
    @GetMapping("/evenements/{id}/intervenants")
    public List<IntervenantResponseDTO> listerParEvenement(@PathVariable("id") Long evenementId) {
        return intervenantService.listerParEvenement(evenementId);
    }

    // PUT /intervenants/{id} — modifier un intervenant
    @PutMapping("/intervenants/{id}")
    public IntervenantResponseDTO modifier(@PathVariable Long id,
                                           @Valid @RequestBody IntervenantUpdateDTO dto) {
        return intervenantService.modifier(id, dto);
    }

    // DELETE /intervenants/{id} — supprimer un intervenant
    @DeleteMapping("/intervenants/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        intervenantService.supprimer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
