package com.unievt.controller;

import com.unievt.dto.InscriptionCreateDTO;
import com.unievt.dto.InscriptionResponseDTO;
import com.unievt.service.InscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;

    @PostMapping
    public ResponseEntity<InscriptionResponseDTO> creer(@Valid @RequestBody InscriptionCreateDTO dto) {
        InscriptionResponseDTO created = inscriptionService.creerInscription(dto);
        return ResponseEntity
                .created(URI.create("/inscriptions/" + created.getId()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<InscriptionResponseDTO>> lister() {
        return ResponseEntity.ok(inscriptionService.listerInscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscriptionResponseDTO> lire(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.lireInscription(id));
    }

    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<InscriptionResponseDTO> confirmer(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.confirmer(id));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<InscriptionResponseDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.annuler(id));
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<String> genererQRCode(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.genererQRCode(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        inscriptionService.supprimerInscription(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
