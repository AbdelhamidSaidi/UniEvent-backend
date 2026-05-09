package com.unievt.controller;

import com.unievt.dto.ClubCreateDTO;
import com.unievt.dto.ClubResponseDTO;
import com.unievt.dto.ClubUpdateDTO;
import com.unievt.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    // POST /clubs — créer un club
    @PostMapping
    public ResponseEntity<ClubResponseDTO> creer(@Valid @RequestBody ClubCreateDTO dto) {
        ClubResponseDTO created = clubService.creer(dto);
        return ResponseEntity
                .created(URI.create("/clubs/" + created.getId()))
                .body(created);
    }

    // GET /clubs — lister tous les clubs
    @GetMapping
    public List<ClubResponseDTO> lister() {
        return clubService.lister();
    }

    // GET /clubs/{id} — lire un club
    @GetMapping("/{id}")
    public ClubResponseDTO lire(@PathVariable Long id) {
        return clubService.lire(id);
    }

    // PUT /clubs/{id} — modifier un club
    @PutMapping("/{id}")
    public ClubResponseDTO modifier(@PathVariable Long id,
                                    @Valid @RequestBody ClubUpdateDTO dto) {
        return clubService.modifier(id, dto);
    }

    // DELETE /clubs/{id} — supprimer un club
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        clubService.supprimer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
