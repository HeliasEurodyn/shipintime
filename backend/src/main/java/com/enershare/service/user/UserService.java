package com.enershare.service.user;

import com.enershare.dto.user.UserDTO;
import com.enershare.dto.user.UserDocumentDTO;
import com.enershare.enums.Role;
import com.enershare.exception.ApplicationException;
import com.enershare.exception.EmailAlreadyExistsException;
import com.enershare.mapper.UserMapper;
import com.enershare.model.user.User;
import com.enershare.model.user.UserDocument;
import com.enershare.repository.user.UserDocumentRepository;
import com.enershare.repository.user.UserRepository;
import com.enershare.service.auth.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final UserDocumentRepository userDocumentRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserDocumentRepository userDocumentRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.userDocumentRepository = userDocumentRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public long getTotalUsers() {
        return userRepository.count();
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
                .isActive(userDTO.isActive())
                .build();

        userRepository.save(user);
    }

//    public void updateUser(@RequestBody UserDTO userDTO) {
//        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
//        User existingUser = optionalUser.orElseThrow(() -> new ApplicationException("1001","User Not Found By Id"));
//
//        String newEmail = userDTO.getEmail();
//        if (!existingUser.getEmail().equals(newEmail)) {
//            Optional<User> userWithNewEmail = userRepository.findByEmail(newEmail);
//            if (userWithNewEmail.isPresent() && !userWithNewEmail.get().equals(existingUser)) {
//                throw new EmailAlreadyExistsException("Email already exists");
//            }
//        }
//
//        existingUser.setEmail(userDTO.getEmail());
//        existingUser.setFirstname(userDTO.getFirstname());
//        existingUser.setLastname(userDTO.getLastname());
//        existingUser.setRole(userDTO.getRole());
//
//        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
//            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
//            existingUser.setPassword(hashedPassword);
//        }
//
//        userRepository.save(existingUser);
//    }


    @Transactional
    @Modifying
    public void syncForce(List<UserDTO> userDTOs) {

        userDTOs.forEach(u ->
                {
                    u.setRole(Role.USER);
                    u.setTermsAccepted(false);
                }
        );

        List<User> usersToSave = this.userMapper.map(userDTOs);

        int batchSize = 100; // Adjust batch size based on performance testing
        for (int i = 0; i < usersToSave.size(); i += batchSize) {
            List<User> batch = usersToSave.subList(i, Math.min(i + batchSize, usersToSave.size()));
            batch.parallelStream()
                    .filter(u -> u.getPassword() != null && !u.getPassword().isEmpty())
                    .forEach(u -> u.setPassword(passwordEncoder.encode(u.getPassword())));
        }

        userRepository.saveAll(usersToSave);
    }

    @Transactional
    public void sync(List<UserDTO> userDTOs) {

        List<String> s1IdList = userDTOs.stream()
                .map(UserDTO::getS1Id)
                .collect(Collectors.toList());

        List<String> existingS1Ids = userRepository.findExistingS1Ids(s1IdList);

        List<UserDTO> usersToSync = userDTOs.stream()
                .filter(dto -> !existingS1Ids.contains(dto.getS1Id()))
                .collect(Collectors.toList());

        usersToSync.forEach(u ->
                {
                    u.setRole(Role.USER);
                    u.setTermsAccepted(false);
                }
        );

        List<User> usersToSave = this.userMapper.map(usersToSync);

        int batchSize = 100; // Adjust batch size based on performance testing
        for (int i = 0; i < usersToSave.size(); i += batchSize) {
            List<User> batch = usersToSave.subList(i, Math.min(i + batchSize, usersToSave.size()));
            batch.parallelStream()
                    .filter(u -> u.getPassword() != null && !u.getPassword().isEmpty())
                    .forEach(u -> u.setPassword(passwordEncoder.encode(u.getPassword())));
        }

        userRepository.saveAll(usersToSave);
    }

    @Transactional
    @Modifying
    public Map saveDocument(MultipartFile file, String type) throws IOException {
        String userId = jwtService.getUserId();
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        byte[] data = file.getBytes();

        UserDocument userDocument = UserDocument.builder()
                .userId(userId)
                .documentType(type)
                .filename(fileName)
                .filetype(fileType)
                .document(data)
                .build();

        userDocumentRepository.deleteAllByDocumentType(type);
        UserDocument saved = userDocumentRepository.save(userDocument);
        return Collections.singletonMap("id", saved.getId());
    }

    public List<UserDocumentDTO> getDocuments() {
        String userId = jwtService.getUserId();
       return userDocumentRepository.getDocuments(userId);
    }

    public UserDTO getCurrent() {
        String userId = jwtService.getUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new ApplicationException("1001","User Not Found By Id"));
        return userMapper.map(user);
    }

    public UserDocument getDocument(String id) {
        return userDocumentRepository.findById(id).orElseThrow(() -> new ApplicationException("0","Document Not Found By Id"));
    }

    @Transactional
    @Modifying
    public void termsAccepted() {
        String userId = jwtService.getUserId();
        this.userRepository.setTermsAccepetd(userId);
    }

    @Transactional
    @Modifying
    public void updateLanguage(String language) {
        String userId = jwtService.getUserId();
        this.userRepository.updateLanguage(userId, language);
    }

    @Transactional
    @Modifying
    public List<UserDTO> findByIds(List<String> ids) {
        List<User> users =  this.userRepository.findByIds(ids);
        return this.userMapper.maplist(users);
    }

}


