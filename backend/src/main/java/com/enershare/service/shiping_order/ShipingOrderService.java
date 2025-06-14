package com.enershare.service.shiping_order;

import com.enershare.dto.apifon.Message;
import com.enershare.dto.apifon.SmsRequest;
import com.enershare.dto.apifon.Subscriber;
import com.enershare.dto.shiping_order.CameraTrackDTO;
import com.enershare.dto.shiping_order.ShipingOrderDTO;
import com.enershare.dto.shiping_order.ShipingOrderLoadDTO;
import com.enershare.dto.user.UserDTO;
import com.enershare.exception.ApplicationException;
import com.enershare.mapper.shiping_order.ShipingOrderMapper;
import com.enershare.model.shiping_order.ShipingOrder;
import com.enershare.model.user.User;
import com.enershare.repository.shiping_order.ShipingOrderRepository;
import com.enershare.repository.user.UserRepository;
import com.enershare.rest.apifon.ApifonRest;
import com.enershare.rest.s1.shiping_order.ShipingOrderRest;
import com.enershare.service.auth.JwtService;
import com.enershare.service.camera_track.CameraTrackService;
import com.enershare.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipingOrderService {

    @Autowired
    private ShipingOrderMapper shipingOrderMapper;

    @Autowired
    private ShipingOrderRepository shipingOrderRepository;

    @Autowired
    private UserService userService;

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


    @Autowired
    private CameraTrackService cameraTrackService;

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

    public List<ShipingOrderDTO> getAllOnPeriod(Instant from, Instant to) {
        List<ShipingOrder> list = shipingOrderRepository.getAllOnPeriod(from, to);
        List<ShipingOrderDTO> dtos = shipingOrderMapper.map(list);

        dtos.sort(
                Comparator.comparing(ShipingOrderDTO::isLoading)
                        .reversed()
                        .thenComparing(
                                Comparator.comparing(ShipingOrderDTO::getLoadingDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        )
                        .thenComparing(ShipingOrderDTO::isCheckedIn)
                        .reversed()
                        .thenComparing(
                                Comparator.comparing(ShipingOrderDTO::getCheckedInDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        )
        );

        List<String> uniqueOwnerIds = dtos.stream()
                .map(ShipingOrderDTO::getOwnerId)
                .distinct()
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toList());

        List<UserDTO> users =  this.userService.findByIds(uniqueOwnerIds);

        Map<String, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::getId, user -> user)); // Map<ownerId, User>


        dtos.forEach(dto -> {
            UserDTO user = userMap.get(dto.getOwnerId());
            if (user != null) {
                dto.setUser(user); // Assuming you have a `setUser` method in ShipingOrderDTO
            }
        });

        return dtos;
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
    public void checkInByCameraTrack(List<CameraTrackDTO> cameraTracks) throws JsonProcessingException {

        cameraTrackService.registerTracks(cameraTracks);

        List<Map<String, String>> results = this.findShipingOrdersByPlates(cameraTracks);

        for (Map<String, String> result : results) {
            this.shipingOrderRepository.suCheckIn(result.get("id"));
            this.shipingOrderRest.checkIn(result.get("s1id"), "");
        }

        List<Map<String, String>> resultsWithPhone = results.stream()
                .filter(r -> r.get("phone") != null && !r.get("phone").isBlank())
                .collect(Collectors.toList());

        for (Map<String, String> result : resultsWithPhone) {

            String token = apifonRest.auth();
            String smsMessage = "Καλώς ήρθατε στην AgroHellas! Ο κωδικός παραλαβής παραγγελιών σας είναι ο " + result.get("s1id");

            if(result.get("language").equals("GB")){
                smsMessage = "Welcome to AgroHellas! Your order pickup code is " + result.get("s1id");
            } else if(result.get("language").equals("AL")){
                smsMessage = "Mirë se erdhët në AgroHellas! Kodi juaj i tërheqjes së porosisë është " + result.get("s1id");
            } else if(result.get("language").equals("BG")){
                smsMessage = "Добре дошли в AgroHellas! Вашият код за получаване на поръчката е " + result.get("s1id");
            } else if(result.get("language").equals("NMC")) { // Added Macedonian language support
                smsMessage = "Добредојдовте во AgroHellas! Вашиот код за подигање на нарачката е " + result.get("s1id");
            }

            Message message = Message.builder()
                    .text(smsMessage)
                    .sender_id("ShipInTime")
                    .dc(2)
                    .build();

            String phoneNumber = result.get("phone");
            String prefix = User.getPrefixNumberByCode(result.get("phone_prefix"));

            Subscriber subscriber = Subscriber.builder()
                    .number(prefix + phoneNumber)
                    .build();

            SmsRequest smsRequest =
                    SmsRequest.builder()
                            .message(message)
                            .subscribers(new Subscriber[]{subscriber})
                            .build();

            apifonRest.sendSms(token, smsRequest);
        }
    }

    public  List<Map<String, String>> findShipingOrdersByPlates(List<CameraTrackDTO> cameraTracks) {
        if (cameraTracks == null || cameraTracks.isEmpty()) {
            return new ArrayList<>();
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.of("UTC"));

        List<String> whereParts = new ArrayList<>();

        for (CameraTrackDTO cameraTrack : cameraTracks) {
            String formattedDate = dateFormatter.format(cameraTrack.getTracktime()); // Extract only the date

            String where = "( s.truck = '" + cameraTrack.getPlate() + "' " +
                    "AND DATE(s.shiping_date) = '" + formattedDate + "' " +
                    "AND s.checked_in = 0)";

            whereParts.add(where);
        }

        // Construct the full query safely
        String queryStr = " SELECT s.id, s.s1id, ifnull(u.language,'') AS language, ifnull(u.phone,'') AS phone, ifnull(u.phone_prefix,'') AS phone_prefix FROM shiping_order s "+
                          " LEFT OUTER JOIN user u ON u.id = s.owner_id WHERE " + String.join(" OR ", whereParts);
        Query query = entityManager.createNativeQuery(queryStr);

        List<Object[]> results = query.getResultList();

        // Convert results to List of Maps
        List<Map<String, String>> orderList = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, String> orderData = new HashMap<>();
            orderData.put("id", row[0].toString());
            orderData.put("s1id", row[1].toString());
            orderData.put("language", row[2].toString());
            orderData.put("phone", row[3] != null ? row[3].toString() : ""); // Handle potential null values
            orderData.put("phone_prefix", row[4] != null ? row[4].toString() : "");

            orderList.add(orderData);
        }

        return orderList;
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
    public void load(ShipingOrderLoadDTO shipingOrdersDTO) {
        this.shipingOrderRepository.loadWithRamp(shipingOrdersDTO.getId(),shipingOrdersDTO.getRamp(),shipingOrdersDTO.getRampTotal());

        ShipingOrder shipingOrder = this.shipingOrderRepository.findById(shipingOrdersDTO.getId()).orElseThrow(() -> new ApplicationException("2000","Order Not Found By Id"));
        User user = this.userRepository.findById(shipingOrder.getOwnerId()) .orElseThrow(() -> new ApplicationException("1001","User Not Found By Id"));

        String token = apifonRest.auth();


        String smsMessage;
        int rampsCount = shipingOrdersDTO.getRampTotal(); // Assuming getRamp() returns a list or count of ramps


        if (rampsCount > 1) {
            smsMessage = "Η παραγγελία σας με αριθμό " + shipingOrder.getS1Number() +
                    " είναι έτοιμη προς φόρτωση, σας αναμένουμε στις ράμπες φόρτωσης " +
                    shipingOrdersDTO.getRamp() + " σε " + shipingOrdersDTO.getShipInMinutes() + " λεπτά.";
        } else {
            smsMessage = "Η παραγγελία σας με αριθμό " + shipingOrder.getS1Number() +
                    " είναι έτοιμη προς φόρτωση, σας αναμένουμε στη ράμπα φόρτωσης " +
                    shipingOrdersDTO.getRamp() + " σε " + shipingOrdersDTO.getShipInMinutes() + " λεπτά.";
        }

        if (user.getLanguage().equals("GB")) {
            if (rampsCount > 1) {
                smsMessage = "Your order with number " + shipingOrder.getS1Number() +
                        " is ready for loading, we are expecting you at the loading ramps " +
                        shipingOrdersDTO.getRamp() + " in " + shipingOrdersDTO.getShipInMinutes() + " minutes.";
            } else {
                smsMessage = "Your order with number " + shipingOrder.getS1Number() +
                        " is ready for loading, we are expecting you at the loading ramp " +
                        shipingOrdersDTO.getRamp() + " in " + shipingOrdersDTO.getShipInMinutes() + " minutes.";
            }
        } else if (user.getLanguage().equals("AL")) {
            if (rampsCount > 1) {
                smsMessage = "Porosia juaj me numër " + shipingOrder.getS1Number() +
                        " është gati për ngarkim, ju presim te rampat e ngarkimit " +
                        shipingOrdersDTO.getRamp() + " pas " + shipingOrdersDTO.getShipInMinutes() + " minutash.";
            } else {
                smsMessage = "Porosia juaj me numër " + shipingOrder.getS1Number() +
                        " është gati për ngarkim, ju presim te rampa e ngarkimit " +
                        shipingOrdersDTO.getRamp() + " pas " + shipingOrdersDTO.getShipInMinutes() + " minutash.";
            }
        } else if (user.getLanguage().equals("BG")) {
            if (rampsCount > 1) {
                smsMessage = "Вашата поръчка с номер " + shipingOrder.getS1Number() +
                        " е готова за товарене, очакваме ви на рампите за товарене " +
                        shipingOrdersDTO.getRamp() + " след " + shipingOrdersDTO.getShipInMinutes() + " минути.";
            } else {
                smsMessage = "Вашата поръчка с номер " + shipingOrder.getS1Number() +
                        " е готова за товарене, очакваме ви на рампата за товарене " +
                        shipingOrdersDTO.getRamp() + " след " + shipingOrdersDTO.getShipInMinutes() + " минути.";
            }
        } else if (user.getLanguage().equals("NMC")) {
            if (rampsCount > 1) {
                smsMessage = "Вашата нарачка со број " + shipingOrder.getS1Number() +
                        " е подготвена за вчитување, ве очекуваме на рампите за вчитување " +
                        shipingOrdersDTO.getRamp() + " за " + shipingOrdersDTO.getShipInMinutes() + " минути.";
            } else {
                smsMessage = "Вашата нарачка со број " + shipingOrder.getS1Number() +
                        " е подготвена за вчитување, ве очекуваме на рампата за вчитување " +
                        shipingOrdersDTO.getRamp() + " за " + shipingOrdersDTO.getShipInMinutes() + " минути.";
            }
        }
        Message message = Message.builder()
                .text(smsMessage)
                .sender_id("ShipInTime")
                .dc(2)
                .build();

        String phoneNumber = user.getPhone(); // String phoneNumber = (user.getPhone().length()>10)?user.getPhone(): "30"+user.getPhone();
        String prefix = User.getPrefixNumberByCode(user.getPhonePrefix());

        Subscriber subscriber = Subscriber.builder()
                .number(prefix + phoneNumber)
                .build();

        SmsRequest smsRequest =
                SmsRequest.builder()
                        .message(message)
                        .subscribers(new Subscriber[]{subscriber})
                        .build();

        apifonRest.sendSms(token, smsRequest);
    }

    @Transactional
    @Modifying
    public void creationNotify(String id) {

        ShipingOrder shipingOrder = this.shipingOrderRepository.findById(id).orElseThrow(() -> new ApplicationException("2000","Order Not Found By Id"));
        User user = this.userRepository.findById(shipingOrder.getOwnerId()) .orElseThrow(() -> new ApplicationException("1001","User Not Found By Id"));

        String token = apifonRest.auth();

        String smsMessage;

        LocalDate shippingDate = shipingOrder.getShipingDate().atZone(ZoneId.systemDefault()).toLocalDate();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = shippingDate.format(dateFormatter);

        DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate2 = shippingDate.format(dateFormatter2);

        smsMessage = "Η αποστολή με αριθμό " + shipingOrder.getS1Number() + " " +
                "έχει δρομολογηθεί για " + formattedDate + ". " +
                "Κατά την άφιξη σας επισκεφθείτε το link https://shipintime.gr/order-date/" + formattedDate2 ;

        if (user.getLanguage().equals("GB")) {

            smsMessage = "Your order with number " + shipingOrder.getS1Number() +
                    " has been scheduled for " +  formattedDate + ". " +
                    "Upon your arrival, please check in https://shipintime.gr/order-date/" + formattedDate2 ;

        } else if (user.getLanguage().equals("AL")) {

            smsMessage = "Porosia juaj me numër " + shipingOrder.getS1Number() +
                    " është planifikuar për " + formattedDate + ". " +
                    "Me mbërritjen tuaj, ju lutemi kontrolloni https://shipintime.gr/order-date/" + formattedDate2 ;

        } else if (user.getLanguage().equals("BG")) {

            smsMessage = "Вашата поръчка с номер " + shipingOrder.getS1Number() +
                    " е насрочена за " + formattedDate + ". " +
                    "При пристигането ви, моля, проверете https://shipintime.gr/order-date/" + formattedDate2 +" тук.";

        } else if (user.getLanguage().equals("NMC")) { // Added Macedonian language support
            smsMessage = "Вашата нарачка со број " + shipingOrder.getS1Number() +
                    " е закажана за " + formattedDate + ". " +
                    "При вашето пристигнување, ве молиме проверете https://shipintime.gr/order-date/" + formattedDate2 +" тука.";
        }

        Message message = Message.builder()
                .text(smsMessage)
                .sender_id("ShipInTime")
                .dc(2)
                .build();

        String phoneNumber = user.getPhone(); //String phoneNumber = (user.getPhone().length()>10)?user.getPhone(): "30"+user.getPhone();
        String prefix = User.getPrefixNumberByCode(user.getPhonePrefix());

        Subscriber subscriber = Subscriber.builder()
                .number(prefix + phoneNumber)
                .build();

        SmsRequest smsRequest =
                SmsRequest.builder()
                        .message(message)
                        .subscribers(new Subscriber[]{subscriber})
                        .build();

        apifonRest.sendSms(token, smsRequest);
    }



}
