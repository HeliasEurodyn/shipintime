package com.enershare.model.user;

import com.enershare.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "user_document")
public class UserDocument {

    @Id
    @Column( updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "filename")
    private String filename;

    @Column(name = "filetype")
    private String filetype;

    @Lob
    @Column(name = "document",  columnDefinition = "MEDIUMBLOB")
    private byte[] document;

    @PrePersist
    @PreUpdate
    void setIdIfMissing() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
