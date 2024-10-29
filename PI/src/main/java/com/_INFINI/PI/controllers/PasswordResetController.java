package com._INFINI.PI.controllers;

import com._INFINI.PI.services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/passwordreset")
@CrossOrigin("*")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/sendresetcode")
    public ResponseEntity<String> sendResetCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        if (passwordResetService.sendResetCode(email)) {
            return ResponseEntity.ok("Code de réinitialisation envoyé avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }
    }


    @PostMapping("/resetpassword")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String resetCode = requestBody.get("resetCode");
        String newPassword = requestBody.get("newPassword");

        if (email == null || resetCode == null || newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tous les paramètres doivent être fournis.");
        }

        if (passwordResetService.resetPassword(email, resetCode, newPassword)) {
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Échec de la réinitialisation du mot de passe. Vérifiez votre code de réinitialisation.");
        }
    }

}
