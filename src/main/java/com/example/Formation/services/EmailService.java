package com.example.Formation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envoie un email simple avec une adresse, un sujet et un contenu spécifiés.
     *
     * @param toEmail Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param body Contenu du message
     */
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("benjeddouhoucem11@gmail.com"); // Assurez-vous que cette adresse est configurée dans votre SMTP
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email envoyé avec succès à : " + toEmail);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            // Vous pouvez enregistrer cette erreur dans un fichier log si nécessaire
        }
    }
}