package com.enershare.repository.user;

import com.enershare.dto.user.UserDTO;
import com.enershare.dto.user.UserDocumentDTO;
import com.enershare.model.user.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, String> {

    void deleteAllByDocumentType(String type);

    @Query("SELECT new com.enershare.dto.user.UserDocumentDTO(u.id, u.documentType, u.filename, u.filetype) FROM UserDocument u WHERE u.userId =:userid ")
    List<UserDocumentDTO> getDocuments(@Param("userid") String userId);

}
