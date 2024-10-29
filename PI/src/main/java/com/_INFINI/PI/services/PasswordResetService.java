package com._INFINI.PI.services;

import com._INFINI.PI.entities.PasswordResetToken;
import com._INFINI.PI.entities.User;
import com._INFINI.PI.repositories.IUserRepository;
import com._INFINI.PI.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class PasswordResetService {

    private final IUserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public PasswordResetService(IUserRepository userRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder,
                                PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public boolean sendResetCode(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return false; // Aucun utilisateur trouvé
            }

            String resetCode = generateRandomCode();
            user.setResetCode(resetCode);
            userRepository.save(user);

            String subject = "Demande de réinitialisation de mot de passe";
            String message = "Votre code de réinitialisation de mot de passe est : " + resetCode;

            sendEmail(email, subject, message);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyResetCode(String email, String code) {
        User user = userRepository.findByEmail(email);
        return user != null && user.getResetCode() != null && user.getResetCode().equals(code);
    }

    public boolean resetPassword(String email, String resetCode, String newPassword) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null || !user.getResetCode().equals(resetCode)) {
                return false; // Utilisateur non trouvé ou code incorrect
            }

            // Mettre à jour le mot de passe et réinitialiser le code
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetCode(null);
            userRepository.save(user);

            return true; // Retourne true si tout s'est bien passé
        } catch (Exception e) {
            e.printStackTrace(); // Ou loggez l'erreur si nécessaire
            return false; // Retourne false en cas d'erreur
        }
    }

    private String generateRandomCode() {
        // Générer un code aléatoire de 6 chiffres
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


    public void saveConfirmationToken(PasswordResetToken token) {
        passwordResetTokenRepository.save(token);
    }

    public Optional<PasswordResetToken> getToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return passwordResetTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }

    public void deleteAllTokens(){
        passwordResetTokenRepository.deleteAll();
    }
}
