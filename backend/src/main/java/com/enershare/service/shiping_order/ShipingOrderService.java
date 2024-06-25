package com.enershare.service.shiping_order;

import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.mapper.shiping_order.ShipingOrderMapper;
import com.enershare.model.shiping_order.ShipingOrder;
import com.enershare.repository.shiping_order.ShipingOrderRepository;
import com.enershare.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ShipingOrderService {

    @Autowired
    private ShipingOrderMapper shipingOrderMapper;

    @Autowired
    private ShipingOrderRepository shipingOrderRepository;

    @Autowired
    private JwtService jwtService;

    public List<ShipingOrderDTO> getObject() {
        List<ShipingOrder> list = shipingOrderRepository.findAll();
        return shipingOrderMapper.map(list);
    }

    public ShipingOrderDTO getObject(String id) throws Exception {
        ShipingOrder optionalEntity = shipingOrderRepository.findById(id).orElseThrow(() -> new Exception("Not Exists"));
        ShipingOrderDTO dto = shipingOrderMapper.map(optionalEntity);
        return dto;
    }

    public ShipingOrderDTO postObject(ShipingOrderDTO shipingOrderDTO) {

        ShipingOrder model = shipingOrderMapper.map(shipingOrderDTO);
        if (model.getId() == null) {
            model.setCreatedOn(Instant.now());
            model.setCreatedBy(jwtService.getUserId());
        }
        model.setModifiedOn(Instant.now());
        model.setModifiedBy(jwtService.getUserId());
        ShipingOrder savedData = shipingOrderRepository.save(model);

        return shipingOrderMapper.map(savedData);
    }

    public void deleteObject(String id) throws Exception {
        ShipingOrder optionalEntity = shipingOrderRepository.findById(id).orElseThrow(() -> new Exception("Does Not Exist"));
        shipingOrderRepository.deleteById(optionalEntity.getId());
    }

    public List<ShipingOrderDTO> getOnPeriod(Instant from, Instant to) {
        List<ShipingOrder> list = shipingOrderRepository.getOnPeriod(from, to, jwtService.getUserId());
        return shipingOrderMapper.map(list);
    }
}
