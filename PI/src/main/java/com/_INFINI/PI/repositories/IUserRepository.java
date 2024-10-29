package com._INFINI.PI.repositories;

import com._INFINI.PI.entities.Role;
import com._INFINI.PI.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    //Optional<User> findByEmaill(String email);

    User findByIdUser(Long idUser);
    List<User> findByRole(Role role);
    Optional<User> findById(Long idUser);

    void deleteById(Long idUser);
    //Optional<User> selectById(Long idUser);
    @Transactional
    @Modifying
    @Query("UPDATE User a " + "SET a.password = ?1    WHERE a.email = ?2")
    void resetPassword(String password,String email);


    @Query("SELECT COUNT(u.idUser) FROM User u")
    int countAllUsers();
    Optional<User> findById(int id);
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            //"LOWER(u.dateOfBirth) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.region) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.job) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.role) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> findBy(@Param("keyword") String keyword);
}