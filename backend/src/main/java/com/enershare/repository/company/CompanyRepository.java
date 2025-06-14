package com.enershare.repository.company;


import com.enershare.model.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query("SELECT c.id FROM Company c WHERE c.id IN :idList")
    List<String> findExistingIds(List<String> idList);

    @Query("SELECT c FROM Company c WHERE c.id IN :ids")
    List<Company> findByIds(@Param("ids") List<String> ids);

}
