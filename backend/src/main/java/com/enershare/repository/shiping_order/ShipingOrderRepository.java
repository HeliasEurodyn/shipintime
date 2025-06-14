package com.enershare.repository.shiping_order;

import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.dto.user.UserDTO;
import com.enershare.model.shiping_order.ShipingOrder;
import com.enershare.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShipingOrderRepository extends JpaRepository<ShipingOrder, String> {

    List<ShipingOrder> findAllByOwnerId(String ownerId);

    @Query("SELECT s FROM ShipingOrder s WHERE s.shipingDate >= :from AND  s.shipingDate <= :to AND s.ownerId = :owner ORDER BY s.modifiedOn DESC")
    List<ShipingOrder> getOnPeriod(@Param("from") Instant from, @Param("to") Instant to, @Param("owner") String owner);

    @Query("SELECT s FROM ShipingOrder s WHERE s.shipingDate >= :from AND  s.shipingDate <= :to ORDER BY s.modifiedOn DESC")
    List<ShipingOrder> getAllOnPeriod(@Param("from") Instant from, @Param("to") Instant to);


    @Query("SELECT s.s1id FROM ShipingOrder s WHERE s.s1id IN :s1IdList")
    List<String> findExistingS1Ids(List<String> s1IdList);

    @Modifying
    @Transactional
    @Query("UPDATE ShipingOrder SET checkedIn = true, checkedInDate = CURRENT_TIMESTAMP, status = 1 WHERE id = :id AND ownerId = :ownerId")
    void checkIn(@Param("id") String id, @Param("ownerId") String ownerId);

    @Modifying
    @Transactional
    @Query("UPDATE ShipingOrder SET checkedIn = true, checkedInDate = CURRENT_TIMESTAMP, status = 1 WHERE id = :id")
    void suCheckIn(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE ShipingOrder SET loading = true, loadingDate = CURRENT_TIMESTAMP, status = 2 WHERE id = :id")
    void load(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE ShipingOrder SET loading = true, " +
            "loadingDate = CURRENT_TIMESTAMP, " +
            "ramp = :ramp, " +
            "rampTotal = :ramptotal, " +
            "status = 2 WHERE id = :id")
    void loadWithRamp(@Param("id") String id, @Param("ramp") String ramp, @Param("ramptotal") Integer ramptotal);

    @Modifying
    @Transactional
    @Query("UPDATE ShipingOrder SET executed = true, executionDate = CURRENT_TIMESTAMP, status = 3 WHERE id = :id")
    void execute(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE ShipingOrder SET executed = false, loading = false, checkedIn = false, status = 0 WHERE id = :id")
    void reset(@Param("id") String id);

    @Query("SELECT s.s1id FROM ShipingOrder s WHERE s.truck IN :plates AND s.checkedIn = false AND FUNCTION('DATE', s.shipingDate) = :today")
    List<String> findShipingOrdersByPlates(List<String> plates, @Param("today") LocalDate today);

}
