package com.enershare.dto.user;

import com.enershare.dto.base.BaseDTO;
import com.enershare.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserDTO extends BaseDTO {

    private String username;

    private String password;

    private String repeatPassword;

    private String email;

    private String firstname;

    private String lastname;

    private String phone;

    private String s1Id;

    private Role role;

    private String language;

    public UserDTO(String id, String username, String firstname, String lastname, String email,  String phone, Role role) {
        this.setId(id);
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

}
