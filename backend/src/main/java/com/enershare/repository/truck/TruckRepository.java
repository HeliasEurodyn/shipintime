package com.enershare.repository.truck;

import com.enershare.model.truck.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TruckRepository extends JpaRepository<Truck, String> {

    @Query("SELECT t.company.id FROM Truck t WHERE t.user.id = :userId")
    List<String> findCompanyIdsByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM Truck t WHERE t.user.id = :userId")
    List<Truck> findTrucksByUserId(@Param("userId") String userId);

}
