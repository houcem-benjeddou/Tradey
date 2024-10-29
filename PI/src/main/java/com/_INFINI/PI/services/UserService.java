package com._INFINI.PI.services;

import com._INFINI.PI.config.JwtService;
import com._INFINI.PI.entities.*;
import com._INFINI.PI.repositories.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    //private final ChatbotRepository chatbotRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public JavaMailSender emailSender;

    @Override
    public List<User> getAll() {return userRepository.findAll();}

    @Override
    public User add(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateuser(Long idUser, User updateUser) {
        // Recherche de l'utilisateur à mettre à jour.
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + idUser));

        // Mettre à jour les champs modifiables
        user.setFirstname(updateUser.getFirstname());
        user.setLastname(updateUser.getLastname());
        user.setRole(updateUser.getRole());
        user.setDateOfBirth(updateUser.getDateOfBirth());
        user.setGender(updateUser.getGender());
        user.setRegion(updateUser.getRegion());
        user.setJob(updateUser.getJob());
        user.setPhoneNumber(updateUser.getPhoneNumber());

        // Mise à jour du mot de passe, si fourni et non vide
        if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }

        // Enregistrement de l'utilisateur mis à jour dans la base de données
        return userRepository.save(user);
    }


    @Override
    public List<User> getAdmins() {
        return userRepository.findByRole(Role.ADMIN);
    }


    @Override
    public List<User> getTraders() {
        return userRepository.findByRole(Role.TRADER);

    }

    @Override
    public User findUserById(Long idUser) {
        return userRepository.findById(idUser).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> search(String keyword) {
        return userRepository.findBy(keyword);
    }




    @Override
    @Transactional
    public void deleteUser(Long idUser) {
        userRepository.deleteById(idUser);
        if (tokenRepository.existsByUserId(idUser)) {
            // Supprimer tous les enregistrements dans la table token liés à cet utilisateur
            tokenRepository.deleteByUserId(idUser);
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<User> list) {
        userRepository.deleteAll();
        for (User user : list) {
            if (tokenRepository.existsByUserId(user.getId())) {
                tokenRepository.deleteByUserId(user.getId());
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username);
        //.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur : " + username));
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest, String token) {
        String email = jwtService.extractUsername(token);
        if (email != null) {
            Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                String currentPassword = changePasswordRequest.getCurrentPassword();
                String newPassword = changePasswordRequest.getNewPassword();

                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    throw new IllegalArgumentException("Mot de passe actuel incorrect");
                }

                String encodedNewPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedNewPassword);
                userRepository.save(user);
            } else {
                throw new UsernameNotFoundException("Utilisateur non trouvé avec l'e-mail : " + email);
            }
        } else {
            throw new IllegalArgumentException("Impossible d'extraire l'e-mail à partir du token JWT");
        }
    }


    @Override
    public int sendAttachmentEmail(String receiverEmail, String resetUrl) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        boolean multipart = true;
        MimeMessageHelper helper = new MimeMessageHelper(message, multipart, "utf-8");

        // Generate a random code to include in the email body
        int max = 999999;
        int min = 9999;
        SecureRandom secureRandom = new SecureRandom();
        int randomCode = secureRandom.nextInt(max - min + 1) + min;

        // Build the HTML content of the email body
        String htmlMsg = "<h3>Réinitialisation du mot de passe pour votre compte</h3>"
                + "<p>Veuillez cliquer sur le lien suivant pour réinitialiser votre mot de passe :</p>"
                + "<p><a href='" + resetUrl + "'>" + resetUrl + "</a></p>"
                + "<p>Utilisez ce code pour valider votre demande de réinitialisation : " + randomCode + "</p>";

        // Définir les propriétés du message e-mail
        message.setContent(htmlMsg, "text/html");
        helper.setTo(receiverEmail);
        helper.setSubject("Demande de réinitialisation de mot de passe pour votre compte");

        // Envoyer l'e-mail
        emailSender.send(message);

        return randomCode;
    }


    /* public String getCurrentUser() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !authentication.isAuthenticated()) {
             return null;
         }
         Object principal = authentication.getPrincipal();
         if (principal instanceof UserDetails) {
             UserDetails userDetails = (UserDetails) principal;
             // Ici, vous pouvez choisir de retourner le nom d'utilisateur ou l'email en fonction de votre implémentation UserDetails
             return userDetails.getUsername(); // Retourne le nom d'utilisateur
             // Ou bien :
             // return userDetails.getEmail(); // Si votre implémentation UserDetails possède une méthode getEmail()
         } else {
             return principal.toString();
         }
     }*/
    public User getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            log.debug("No valid authentication found for user.");
            throw new UsernameNotFoundException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            log.debug("Principal is not an instance of UserDetails: {}", principal.getClass().getName());
            throw new UsernameNotFoundException("User details not found");
        }

        String email = ((UserDetails) principal).getUsername();
        log.debug("Retrieving user by email: {}", email);
        User user = getUserByEmail(email);
        if (user == null) {
            log.debug("No user found with email: {}", email);
            throw new UsernameNotFoundException("No user found with provided email");
        }
        return user;
    }
}






    /*@Override
    public User assignAccountToUser(Long idUser, Long idAccount) {
        Account account= accountRepository.findAccountByAccountNum(idAccount);
        User user=userRepository.findByIdUser(idUser);
       // user.getAccountList().add(account);
       // account.setUsers(user);

        if(user.getAccounts()==null){
            user.setAccounts((new HashSet<>()));
            user.getAccounts().add(account);
        }
        else
            user.getAccounts().add(account);
        return userRepository.save(user);
    }*/









