package com.unievt.controller;

import com.unievt.dto.EvenementCreateDTO;
import com.unievt.dto.EvenementIntervenantResponseDTO;
import com.unievt.dto.EvenementResponseDTO;
import com.unievt.dto.EvenementUpdateDTO;
import com.unievt.dto.InscriptionResponseDTO;
import com.unievt.mapper.EvenementIntervenantMapper;
import com.unievt.repository.EvenementIntervenantRepository;
import com.unievt.repository.EvenementRepository;
import com.unievt.service.InscriptionService;
import com.unievt.service.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/evenements")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;
    private final InscriptionService inscriptionService;
    private final EvenementRepository evenementRepository;
    private final EvenementIntervenantRepository evenementIntervenantRepository;
    private final EvenementIntervenantMapper evenementIntervenantMapper;

    @PostMapping
    public ResponseEntity<EvenementResponseDTO> creer(@Valid @RequestBody EvenementCreateDTO dto) {
        EvenementResponseDTO created = evenementService.creerEvenement(dto);
        return ResponseEntity
                .created(URI.create("/evenements/" + created.getId()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<EvenementResponseDTO>> lister() {
        return ResponseEntity.ok(evenementService.listerEvenements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvenementResponseDTO> lire(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.lireEvenement(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvenementResponseDTO> modifier(@PathVariable Long id,
                                                         @Valid @RequestBody EvenementUpdateDTO dto) {
        return ResponseEntity.ok(evenementService.modifierEvenement(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        evenementService.supprimerEvenement(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/soumettre")
    public ResponseEntity<EvenementResponseDTO> soumettre(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.soumettre(id));
    }

    @PatchMapping("/{id}/verifier")
    public ResponseEntity<EvenementResponseDTO> verifier(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.verifier(id));
    }

    @PatchMapping("/{id}/approuver")
    public ResponseEntity<EvenementResponseDTO> approuver(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.approuver(id));
    }

    @PatchMapping("/{id}/rejeter")
    public ResponseEntity<EvenementResponseDTO> rejeter(@PathVariable Long id,
                                                        @RequestParam("motif") String motif) {
        return ResponseEntity.ok(evenementService.rejeter(id, motif));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<EvenementResponseDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.annuler(id));
    }

    @PatchMapping("/{id}/archiver")
    public ResponseEntity<EvenementResponseDTO> archiver(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.archiver(id));
    }

    @GetMapping("/{id}/intervenants")
    public ResponseEntity<List<EvenementIntervenantResponseDTO>> listerIntervenants(@PathVariable Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evenement introuvable: " + id);
        }
        List<EvenementIntervenantResponseDTO> intervenants = evenementIntervenantRepository.findByEvenementId(id)
                .stream()
                .map(evenementIntervenantMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(intervenants);
    }

    @GetMapping("/{evenementId}/inscriptions")
    public ResponseEntity<List<InscriptionResponseDTO>> listerInscriptions(@PathVariable Long evenementId) {
        return ResponseEntity.ok(inscriptionService.listerInscriptionsParEvenement(evenementId));
    }
}
