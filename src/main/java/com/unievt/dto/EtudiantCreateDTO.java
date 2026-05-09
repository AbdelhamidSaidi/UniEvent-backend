package com.unievt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtudiantCreateDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    private String photo;
    private String telephone;
    private String filiere;
    private Integer anneeEtude;
    private String cin;

    // No role here. Étudiant identity comes from the etudiant table itself.
    // If an étudiant later needs admin permissions, use PATCH /utilisateurs/{id}/role.
}
