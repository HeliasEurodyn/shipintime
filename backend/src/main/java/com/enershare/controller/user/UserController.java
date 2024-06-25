package com.enershare.controller.user;

import com.enershare.dto.user.UserDTO;
import com.enershare.service.auth.JwtService;
import com.enershare.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<String> testJwt(HttpServletRequest request) {
        String token = jwtService.getJwt(request);
        if (token != null) {
            String userId = jwtService.getUserIdByToken(token);
            return ResponseEntity.ok("User ID: " + userId);
        } else {
            return ResponseEntity.badRequest().body("No JWT token found in the request.");
        }
    }

    @GetMapping("/total-users")
    public long getTotalUsers() {
        return userService.getTotalUsers();
    }

    @GetMapping(path = "/all-users")
    Page<UserDTO> getUsers(@RequestParam int page, @RequestParam int size) {
        return userService.getUsers(page, size);
    }

    @PostMapping("/create")
    public void createUser(@RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
    }

    @DeleteMapping
    public void deleteObject(@RequestParam("id") String id) {
        userService.deleteObject(id);
    }

    @GetMapping(path = "/by-id")
    UserDTO getObject(@RequestParam("id") String id) {
        return userService.getObject(id);
    }

//    @PutMapping("/update")
//    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO) {
//        try {
//            userService.updateUser(userDTO);
//            return ResponseEntity.ok("User updated successfully");
//        } catch (Exception e) {
//            logger.error("Error updating user. UserDTO: {}", userDTO, e);
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("403 Forbidden: Insufficient permissions");
//        }
//    }

    @PutMapping("/update")
    public void updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
    }
    
}
