package com.enershare.service.user;

import com.enershare.dto.user.UserDTO;
import com.enershare.exception.EmailAlreadyExistsException;
import com.enershare.mapper.UserMapper;
import com.enershare.model.user.User;
import com.enershare.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;


@Service
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public Page<UserDTO> getUsers(int page, int size) {
        return userRepository.getUsers(PageRequest.of(page, size));
    }

    public UserDTO getObject(String id) {
        User user = userRepository.findById(id)
                .orElseThrow();
        UserDTO dto = userMapper.map(user);
        dto.setPassword(null);
        return dto;
    }

    public void deleteObject(String id) {
        User optionalEntity = userRepository.findById(id)
                .orElseThrow();

        userRepository.deleteById(optionalEntity.getId());
    }

    public void createUser(@RequestBody UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = User.builder()
                .firstname(userDTO.getFirstname())
                .lastname(userDTO.getLastname())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(userDTO.getRole())
                .build();

        userRepository.save(user);
    }

    public void updateUser(@RequestBody UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        User existingUser = optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newEmail = userDTO.getEmail();
        if (!existingUser.getEmail().equals(newEmail)) {
            Optional<User> userWithNewEmail = userRepository.findByEmail(newEmail);
            if (userWithNewEmail.isPresent() && !userWithNewEmail.get().equals(existingUser)) {
                throw new EmailAlreadyExistsException("Email already exists");
            }
        }

        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstname(userDTO.getFirstname());
        existingUser.setLastname(userDTO.getLastname());
        existingUser.setRole(userDTO.getRole());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
            existingUser.setPassword(hashedPassword);
        }

        userRepository.save(existingUser);
    }

}


