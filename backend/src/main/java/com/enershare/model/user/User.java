package com.enershare.model.user;

import com.enershare.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @Column( updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    private String username;

    private String password;

    private String repeatPassword;

    private String email;

    private String firstname;

    private String lastname;

    private String phone;

    private String s1Id;

    @Column(updatable = false)
    private String logincode;

    @Column(updatable = false)
    private Instant loginRequestDate;

    @Column( updatable = false)
    private String language;

    @Column
    private String phonePrefix;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(updatable = false)
    private boolean termsAccepted;

    private boolean isActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }


    @PrePersist
    @PreUpdate
    void setIdIfMissing() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public static String getPrefixNumberByCode(String code){
        if(code.equals("GB")) return "0044";
        if(code.equals("EL")) return "0030";
        if(code.equals("AL")) return "00355";
        if(code.equals("BG")) return "00359";
        if(code.equals("NMC")) return "00389";
        return "0030";
    }

}
