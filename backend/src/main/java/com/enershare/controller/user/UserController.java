package com.enershare.controller.user;

import com.enershare.dto.user.UserDTO;
import com.enershare.dto.user.UserDocumentDTO;
import com.enershare.exception.ApplicationException;
import com.enershare.model.user.UserDocument;
import com.enershare.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sync")
    public void sync(@RequestBody List<UserDTO> userDTOs) {
        userService.sync(userDTOs);
    }

    @PostMapping("/sync/force")
    public void syncForce(@RequestBody List<UserDTO> userDTOs) {
        userService.syncForce(userDTOs);
    }

    @PostMapping("/document")
    public Map document(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) throws IOException {
            return userService.saveDocument(file, type);
    }
    @GetMapping("/documents")
    public List<UserDocumentDTO> documents() {
            return userService.getDocuments();
    }

    @GetMapping("/current")
    public UserDTO getCurrent() {
        return userService.getCurrent();
    }

    @GetMapping("/document/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String id) {
        UserDocument userDocument = userService.getDocument(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + userDocument.getFilename())
                .contentType(MediaType.parseMediaType(userDocument.getFiletype()))
                .contentLength(userDocument.getDocument().length)
                .body(new ByteArrayResource(userDocument.getDocument()));
    }

    @PostMapping("/terms-accepted")
    public ResponseEntity<Void> termsAccepted() {
        userService.termsAccepted();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/language/{language}")
    public ResponseEntity<Void> updateLanguage(@PathVariable String language) {
        userService.updateLanguage(language);
        return ResponseEntity.ok().build();
    }

}
