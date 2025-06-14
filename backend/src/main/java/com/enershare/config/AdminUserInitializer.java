package com.enershare.config;

import com.enershare.dto.user.UserDTO;
import com.enershare.enums.Role;
import com.enershare.repository.user.UserRepository;
import com.enershare.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail("admin")) {
            UserDTO adminUserDTO = new UserDTO();
            adminUserDTO.setFirstname("admin");
            adminUserDTO.setLastname("admin");
            adminUserDTO.setEmail("admin");
            adminUserDTO.setPassword("admin");
            adminUserDTO.setUsername("admin");
            adminUserDTO.setRole(Role.ADMIN);
            adminUserDTO.setActive(true);
            userService.createUser(adminUserDTO);
        }
    }
}
