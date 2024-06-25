package com.enershare.repository.user;

import com.enershare.dto.user.UserDTO;
import com.enershare.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);


    @Query("SELECT new com.enershare.dto.user.UserDTO(u.id, u.username, u.firstname, u.lastname, u.email, u.phone, u.role) FROM User u ")
    Page<UserDTO> getUsers(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);
}
