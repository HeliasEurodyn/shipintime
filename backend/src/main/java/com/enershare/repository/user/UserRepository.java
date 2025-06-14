package com.enershare.repository.user;

import com.enershare.dto.user.UserDTO;
import com.enershare.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

//    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndIsActive(String username, boolean isActive);

//    Optional<User> findFirstByPhone(String username);

    Optional<User> findFirstByPhoneAndIsActiveAndPhonePrefix(String username, boolean isActive, String prefix);
    @Query("SELECT u FROM User u WHERE u.username = :name OR u.phone = :name AND u.isActive = true")
    Optional<User> findByUsernameOrPhone(String name);

    @Query("SELECT new com.enershare.dto.user.UserDTO(u.id, u.username, u.firstname, u.lastname, u.email, u.phone, u.role) FROM User u ")
    Page<UserDTO> getUsers(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Transactional
    @Modifying
    @Query(" UPDATE User SET loginRequestDate = now(), logincode = :logincode WHERE id = :id")
    void setLoginCode(@Param("logincode") String refreshtoken, @Param("id") String id);

    @Query("SELECT u.s1Id FROM User u WHERE u.s1Id IN :s1IdList")
    List<String> findExistingS1Ids(List<String> s1IdList);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("UPDATE User SET termsAccepted = true WHERE id = :userId")
    void setTermsAccepetd(@Param("userId") String userId);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("UPDATE User SET language = :language WHERE id = :userId")
    void updateLanguage(@Param("userId") String userId, @Param("language") String language);


    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(@Param("ids") List<String> ids);
}
