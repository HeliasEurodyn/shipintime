package com.enershare.service.shiping_order;

import com.enershare.dto.apifon.Message;
import com.enershare.dto.apifon.SmsRequest;
import com.enershare.dto.apifon.Subscriber;
import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.exception.ApplicationException;
import com.enershare.mapper.shiping_order.ShipingOrderMapper;
import com.enershare.model.shiping_order.ShipingOrder;
import com.enershare.model.user.User;
import com.enershare.repository.shiping_order.ShipingOrderRepository;
import com.enershare.repository.user.UserRepository;
import com.enershare.rest.apifon.ApifonRest;
import com.enershare.rest.s1.ShipingOrderRest;
import com.enershare.service.auth.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShipingOrderService {

    @Autowired
    private ShipingOrderMapper shipingOrderMapper;

    @Autowired
    private ShipingOrderRepository shipingOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ShipingOrderRest shipingOrderRest;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ApifonRest apifonRest;

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

    @Transactional
    @Modifying
    public void syncForce(List<ShipingOrderDTO> shipingOrdersDTO) {

        List<ShipingOrder> shipingOrders = this.shipingOrderMapper.toEntities(shipingOrdersDTO);

        for (ShipingOrder model : shipingOrders) {
            model.setCreatedOn(Instant.now());
        }

        shipingOrderRepository.saveAll(shipingOrders);

//        for (ShipingOrderDTO dto : shipingOrdersDTO) {
//            String insertQuery = "INSERT INTO shiping_order (id, s1id, s1code, s1number, shiping_date, ins_date, owner_id, s1truck, truck, checked_in, checked_in_date, executed, execution_date) " +
//                    "VALUES (:id, :s1id, :s1code, :s1number, :shipingDate, :insDate, (SELECT id FROM user WHERE s1id = :owner_id), :s1truck, :truck, :checkedIn, :checkedInDate, :executed, :executionDate)";
//
//            Query query = entityManager.createNativeQuery(insertQuery);
//            query.setParameter("id", dto.getS1id());
//            query.setParameter("s1id", dto.getS1id());
//            query.setParameter("s1code", dto.getS1Code());
//            query.setParameter("s1number", dto.getS1Number());
//            query.setParameter("shipingDate", dto.getShipingDate());
//            query.setParameter("insDate", dto.getInsDate());
//            query.setParameter("owner_id", dto.getOwnerId());
//            query.setParameter("s1truck", dto.getS1truck());
//            query.setParameter("truck", dto.getTruck());
//            query.setParameter("checkedIn", dto.isCheckedIn());
//            query.setParameter("checkedInDate", dto.getCheckedInDate());
//            query.setParameter("executed", dto.isExecuted());
//            query.setParameter("executionDate", dto.getExecutionDate());
//
//            query.executeUpdate();
//        }

    }

    @Transactional
    public void sync(List<ShipingOrderDTO> shipingOrdersDTO) {

        List<String> s1IdList = shipingOrdersDTO.stream()
                .map(ShipingOrderDTO::getS1id)
                .collect(Collectors.toList());

        List<String> existingS1Ids = shipingOrderRepository.findExistingS1Ids(s1IdList);

        List<ShipingOrderDTO> ordersToSync = shipingOrdersDTO.stream()
                .filter(dto -> !existingS1Ids.contains(dto.getS1id()))
                .collect(Collectors.toList());

        List<ShipingOrder> shipingOrders = this.shipingOrderMapper.toEntities(ordersToSync);

        for (ShipingOrder model : shipingOrders) {
            model.setCreatedOn(Instant.now());
        }

        shipingOrderRepository.saveAll(shipingOrders);

//        for (ShipingOrderDTO dto : ordersToSync) {
//
//            String insertQuery = "INSERT INTO shiping_order (id, s1id, s1code, s1number, shiping_date, ins_date, owner_id, s1truck, truck, checked_in, checked_in_date, executed, execution_date) " +
//                    "VALUES (:id, :s1id, :s1code, :s1number, :shipingDate, :insDate, (SELECT id FROM user WHERE s1id = :owner_id), :s1truck, :truck, :checkedIn, :checkedInDate, :executed, :executionDate)";
//
//            Query query = entityManager.createNativeQuery(insertQuery);
//            query.setParameter("id", dto.getS1id());
//            query.setParameter("s1id", dto.getS1id());
//            query.setParameter("s1code", dto.getS1Code());
//            query.setParameter("s1number", dto.getS1Number());
//            query.setParameter("shipingDate", dto.getShipingDate());
//            query.setParameter("insDate", dto.getInsDate());
//            query.setParameter("owner_id", dto.getOwnerId());
//            query.setParameter("s1truck", dto.getS1truck());
//            query.setParameter("truck", dto.getTruck());
//            query.setParameter("checkedIn", dto.isCheckedIn());
//            query.setParameter("checkedInDate", dto.getCheckedInDate());
//            query.setParameter("executed", dto.isExecuted());
//            query.setParameter("executionDate", dto.getExecutionDate());
//
//            query.executeUpdate();
//        }
    }

    @Transactional
    @Modifying
    public void checkIn(String id) throws IOException {
        String userId = jwtService.getUserId();
        this.shipingOrderRepository.checkIn(id, userId);

        String clientId = this.shipingOrderRest.login();
        this.shipingOrderRest.checkIn(id,clientId);
    }

    @Transactional
    @Modifying
    public void suCheckIn(String id) {
        this.shipingOrderRepository.suCheckIn(id);
    }

    @Transactional
    @Modifying
    public void suLoad(String id) {
        this.shipingOrderRepository.load(id);
    }

    @Transactional
    @Modifying
    public void execute(String id) {
        this.shipingOrderRepository.execute(id);
    }

    @Transactional
    @Modifying
    public void reset(String id) {
        this.shipingOrderRepository.reset(id);
    }

    @Transactional
    @Modifying
    public void load(String id) {
        this.shipingOrderRepository.load(id);

        ShipingOrder shipingOrder = this.shipingOrderRepository.findById(id).orElseThrow(() -> new ApplicationException("2000","Order Not Found By Id"));
        User user = this.userRepository.findById(shipingOrder.getOwnerId()) .orElseThrow(() -> new ApplicationException("1001","User Not Found By Id"));

        String token = apifonRest.auth();

        String smsMessage = "Η παραγγελία σας με αριθμό "+ shipingOrder.getS1Number() +" είναι έτοιμη προς φόρτωση, σας αναμένουμε στην ράμπα φόρτωσης σε 10 λεπτά";
        if(user.getLanguage().equals("GB")){
            smsMessage = "Your order with number "+ shipingOrder.getS1Number() +" is ready for loading, we are expecting you at the loading ramp in 10 minutes.";
        } else if(user.getLanguage().equals("AL")){
            smsMessage = "Porosia juaj me numër "+ shipingOrder.getS1Number() +" është gati për ngarkim, ju presim te rampa e ngarkimit pas 10 minutash.";
        } else if(user.getLanguage().equals("BG")){
            smsMessage =  "Вашата поръчка с номер "+ shipingOrder.getS1Number() +" е готова за товарене, очакваме ви на рампата за товарене след 10 минути.";
        }

        Message message = Message.builder()
                .text(smsMessage)
                .sender_id("ShipInTime")
                .build();

        String phoneNumber = (user.getPhone().length()>10)?user.getPhone(): "30"+user.getPhone();
//        String phoneNumber =  "306993649230";
        Subscriber subscriber = Subscriber.builder()
                .number(phoneNumber)
                .build();

        SmsRequest smsRequest =
                SmsRequest.builder()
                        .message(message)
                        .subscribers(new Subscriber[]{subscriber})
                        .build();

        apifonRest.sendSms(token, smsRequest);
    }


}
