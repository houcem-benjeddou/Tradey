package com._INFINI.PI.services;

import com._INFINI.PI.entities.ChangePasswordRequest;
import com._INFINI.PI.entities.User;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    List<User> getAll ();
    User add(User user);
    User updateUser(User user);
    //List<User> selectAll();
    List<User> getAdmins();
    List<User> getTraders();



    User findUserById(Long idUser);

    User getUserByEmail(String email);

    User getUserById(int id);
    List<User> search(String keyword);
     //List<String> getNewQuestionsForAdmin(String firstname);
   // double calculRetardPaiement(Long idUser);
   User updateuser(Long idUser, User updateuser);
    //void respondToQuestion(String firstname, int id, String response);
    void deleteUser(Long idUser);
    void deleteAll(List<User> list);

    UserDetails loadUserByUsername(String username);

    void changePassword(ChangePasswordRequest changePasswordRequest, String username);
    //User getById(long idUser);
    //void affecterRoleToUser(Long idRole, Long id);

    int sendAttachmentEmail(String receiverEmail, String resetUrl) throws MessagingException;

    //String getCurrentUser();
    User getCurrentUserDetails();
    //User assignAccountToUser(Long idUser, Long idAccount);

    //public User getConnectedUser();
}
