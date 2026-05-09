package com.unievt.controller;

import com.unievt.dto.UtilisateurCreateDTO;
import com.unievt.dto.UtilisateurResponseDTO;
import com.unievt.dto.UtilisateurUpdateDTO;
import com.unievt.enums.RoleEnum;
import com.unievt.service.UtilisateurService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // POST /utilisateurs — créer un utilisateur
    @PostMapping
    public ResponseEntity<UtilisateurResponseDTO> creer(@Valid @RequestBody UtilisateurCreateDTO dto) {
        UtilisateurResponseDTO created = utilisateurService.creer(dto);
        return ResponseEntity
                .created(URI.create("/utilisateurs/" + created.getId()))
                .body(created);
    }

    // GET /utilisateurs — lister tous les utilisateurs
    @GetMapping
    public List<UtilisateurResponseDTO> lister() {
        return utilisateurService.lister();
    }

    // GET /utilisateurs/{id} — lire un utilisateur
    @GetMapping("/{id}")
    public UtilisateurResponseDTO lire(@PathVariable Long id) {
        return utilisateurService.lire(id);
    }

    // PUT /utilisateurs/{id} — modifier le profil
    @PutMapping("/{id}")
    public UtilisateurResponseDTO modifier(@PathVariable Long id,
                                           @Valid @RequestBody UtilisateurUpdateDTO dto) {
        return utilisateurService.modifier(id, dto);
    }

    // PATCH /utilisateurs/{id}/activer — activate (idempotent, no body)
    @PatchMapping("/{id}/activer")
    public UtilisateurResponseDTO activer(@PathVariable Long id) {
        return utilisateurService.activer(id);
    }

    // PATCH /utilisateurs/{id}/desactiver — deactivate (idempotent, no body)
    @PatchMapping("/{id}/desactiver")
    public UtilisateurResponseDTO desactiver(@PathVariable Long id) {
        return utilisateurService.desactiver(id);
    }

    // PATCH /utilisateurs/{id}/email — change the user's email
    // Body: {"email": "new@unievt.ma"}. 409 if already used by another user.
    @PatchMapping("/{id}/email")
    public UtilisateurResponseDTO changerEmail(@PathVariable Long id,
                                               @Valid @RequestBody EmailChangeRequest body) {
        return utilisateurService.changerEmail(id, body.email());
    }

    // PATCH /utilisateurs/{id}/role — change the user's role (admin op)
    // Forbidden: ETUDIANT (use POST /etudiants instead) and changing FROM ETUDIANT
    @PatchMapping("/{id}/role")
    public UtilisateurResponseDTO changerRole(@PathVariable Long id,
                                              @Valid @RequestBody RoleChangeRequest body) {
        return utilisateurService.changerRole(id, body.role());
    }

    // DELETE /utilisateurs/{id} — supprimer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public record EmailChangeRequest(
            @NotBlank(message = "L'email est obligatoire")
            @Email(message = "Email invalide")
            String email
    ) {}

    public record RoleChangeRequest(@NotNull RoleEnum role) {}
}
