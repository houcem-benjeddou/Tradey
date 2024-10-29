package com._INFINI.PI.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.processing.Pattern;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@ToString
@Builder
public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idUser;

    /*@NonNull
    @Size(min = 6, max = 32)
    String username;*/

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    String password;

    @NonNull
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    Role role;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    String firstname;

    @NotBlank(message = "Le nom ne peut pas être vide")
    String lastname;

    @NotNull()
    @Past(message = "La date de naissance doit être dans le passé")
    LocalDate dateOfBirth;

    @NotNull()
    @Enumerated(EnumType.STRING)
    Gender gender;

    //@NotBlank(message = "La région ne peut pas être vide")
    String region;

    //@NotBlank(message = "Le numéro de téléphone ne peut pas être vide")
    //@Pattern(regexp = "\\d{8}", message = "Le numéro de téléphone doit être composé de 8 chiffres")
    String phoneNumber;

    String job;


    @Temporal(TemporalType.DATE )
    Date createdDate;

    @OneToMany(mappedBy = "user")
    Set<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public Long getId() {return idUser;}

    public String getMail() {
        return email;
    }

    String resetCode;
    public User(String username, String email, String encode) {

    }


    boolean Active;

}
