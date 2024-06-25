package com.enershare.repository.shiping_order;

import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.dto.user.UserDTO;
import com.enershare.model.shiping_order.ShipingOrder;
import com.enershare.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ShipingOrderRepository extends JpaRepository<ShipingOrder, String> {

    List<ShipingOrder> findAllByOwnerId(String ownerId);

//    @Query("SELECT new com.enershare.dto.shiping_order.ShipingOrderDTO(s.id, s.name, s.modifiedOn, s.jsonUrl, s.id, s.name) FROM ShipingOrder s ORDER BY s.modifiedOn DESC")
//    List<ShipingOrderDTO> getObject();

    @Query("SELECT s FROM ShipingOrder s WHERE s.shipingDate >= :from AND  s.shipingDate <= :to AND s.ownerId = :owner ORDER BY s.modifiedOn DESC")
    List<ShipingOrder> getOnPeriod(@Param("from") Instant from, @Param("to") Instant to, @Param("owner") String owner);

}
