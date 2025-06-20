package com.enershare.mapper;

import com.enershare.dto.user.UserDTO;
import com.enershare.model.user.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public abstract UserDTO map(User user);

    public abstract List<UserDTO> maplist(List<User> user);

    public abstract List<User> map(List<UserDTO> user);

    @Mapping(source = "active", target = "isActive")
    public abstract User map(UserDTO user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstname", source = "firstname")
    @Mapping(target = "lastname", source = "lastname")
    @Mapping(target = "role", source = "role")
    public abstract void updateUserDTO(User user, @MappingTarget UserDTO dto);

    @Mapping(source = "password", target = "password", qualifiedByName = "passwordEncoder")
    public abstract void updateUser(UserDTO dto, @MappingTarget User user);

    @Named("passwordEncoder")
    String passwordEncoder(String password) {
        return passwordEncoder.encode(password);
    }


}
