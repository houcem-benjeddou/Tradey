package com._INFINI.PI.controllers;

import com._INFINI.PI.entities.ChangePasswordRequest;
import com._INFINI.PI.entities.User;
import com._INFINI.PI.services.AuthenticationService;
import com._INFINI.PI.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserRestController {

    private final UserService userServices;
private final AuthenticationService authenticationService;
    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);

    @PatchMapping("/changepassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        // Récupérer le token JWT de l'en-tête Authorization
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || token.isBlank() || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT non valide ou manquant");
        }

        // Extraire le token JWT de l'en-tête
        token = token.substring(7); // Supprimer le préfixe "Bearer "

        try {
            // Appeler la méthode changePassword du service d'authentification
            userServices.changePassword(changePasswordRequest, token);
            return ResponseEntity.ok("Mot de passe modifié avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la modification du mot de passe");
        }}



    @PutMapping("/updateUser/{idUser}")
    public ResponseEntity<User> updateuser(@PathVariable Long idUser, @RequestBody User updateUser) {
        try {
            User updatedUser = userServices.updateuser(idUser, updateUser);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    private boolean hasEightDigits(String phoneNumber) {
        // Utilisation d'une expression régulière pour vérifier si la chaîne contient exactement 8 chiffres
        return phoneNumber != null && phoneNumber.matches("\\d{8}");
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userServices.getAll();
    }

    @GetMapping("/{id}")
    /*@PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserValid(authentication, #id)")*/
    public String getUser(@PathVariable Long idUser, Model model) {
        User user = userServices.findUserById(idUser);
        if (user != null) {
            model.addAttribute("user", user);
            return "userDetails"; // Supposons que userDetails.html est la vue pour afficher les détails de l'utilisateur
        } else {
            return "error"; // Supposons que error.html est la vue pour afficher une erreur
        }
    }
    //@GetMapping("/emails")
    //public List<User> getUserEmails(@PathVariable  email) {
       // return userServices.getUserByEmail(email);}
    @PutMapping("/update/{id}")
    /*@PreAuthorize("@userSecurity.isUserValid(authentication, #id)")*/
    public User updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        return userServices.updateUser(user);
    }


    @DeleteMapping("/delete/{id}")
    /*@PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserValid(authentication, #id)")*/
    public void deleteUser(@PathVariable("id") Long idUser) {
        userServices.deleteUser(idUser);
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllUsers() {
        List<User> userList = userServices.getAll();
        userServices.deleteAll(userList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> users = userServices.search(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admins")
    public List<User> getAdmins() {

        List <User> list= userServices.getAdmins();

        return list ;
    }
    @GetMapping("/traders")
    public List<User> getTraders() {

        List <User> list= userServices.getTraders();

        return list ;
    }

    @PostMapping("/ConfirmeCompte/{email}")
    public ResponseEntity<String> Confirme(@PathVariable String email) {
        User u = userServices.getUserByEmail(email);
        if (u != null) {
            u.setActive(true);
            userServices.updateUser(u);
            return ResponseEntity.ok("Le compte de l'utilisateur avec l'email " + email + " a été confirmé avec succès.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

   /* @GetMapping("/session")
    public User getConnectedUser() {
        String currentUserEmail = userServices.getCurrentUser();
        return userServices.getUserByEmail(currentUserEmail);
    }*/
   @GetMapping("/session")
   public ResponseEntity<?> getConnectedUser() {
       try {
           User currentUser = userServices.getCurrentUserDetails();
           return ResponseEntity.ok(currentUser);
       } catch (UsernameNotFoundException e) {
           log.error("Erreur d'authentification: {}", e.getMessage());
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
       } catch (Exception e) {
           log.error("Erreur interne: {}", e.getMessage());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne est survenue");
       }
   }

    @PutMapping("updateuser/{id}")
    public void updateuserbyID(@PathVariable String id,@RequestBody User user)
    {
        User u=userServices.getUserByEmail(id);
        u.setFirstname(user.getFirstname());
        u.setLastname(user.getLastname());
        u.setDateOfBirth(user.getDateOfBirth());
        u.setEmail(user.getEmail());
        u.setGender(user.getGender());
        u.setPhoneNumber(user.getPhoneNumber());
        u.setRegion(user.getRegion());
        u.setJob(user.getJob());
        //u.setNewQuestions(user.getNewQuestions());
        userServices.updateUser(u);
    }


}
