package com.enershare.dto.user;

import com.enershare.dto.base.BaseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserDocumentDTO extends BaseDTO {

    private String documentType;

    private String filename;

    private String filetype;

    public UserDocumentDTO(String id, String documentType, String filename, String filetype ){
        this.setId(id);
        this.documentType = documentType;
        this.filename = filename;
        this.filetype = filetype;
    }

}
