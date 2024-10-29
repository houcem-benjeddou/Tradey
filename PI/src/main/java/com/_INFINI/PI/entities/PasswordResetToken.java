package com._INFINI.PI.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @Column(nullable=false)
    private LocalDateTime expiryDate;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(nullable=false)
    private User users;

    public PasswordResetToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, User user) {
        super();
        this.token = token;
        this.createdAt = createdAt;
        this.expiryDate = expiresAt;
        this.users = user;
    }
}
