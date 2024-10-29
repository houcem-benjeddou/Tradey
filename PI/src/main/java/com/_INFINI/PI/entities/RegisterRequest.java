package com._INFINI.PI.entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotBlank(message = "L'email est requis")
  @Email(message = "L'email doit être valide")
  private String email;
  @NotBlank(message = "Le mot de passe est requis")
  @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
  private String password;
  @NonNull
  private Role role;
  @NotBlank(message = "Le prénom ne peut pas être vide")
  String firstname;
  @NotBlank(message = "Le nom ne peut pas être vide")
  String lastname;
  @NotNull(message = "La date de naissance ne peut pas être vide")
  @Past(message = "La date de naissance doit être dans le passé")
  LocalDate dateOfBirth;
  @Enumerated(EnumType.STRING)
  Gender gender;
  String region;
  @Pattern(regexp = "\\d{8}", message = "Le numéro de téléphone doit être composé de 8 chiffres")
  String phoneNumber;
  String job;
}
