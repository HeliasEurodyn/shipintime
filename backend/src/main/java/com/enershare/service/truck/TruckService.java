package com.enershare.service.truck;

import com.enershare.dto.company.CompanyDTO;
import com.enershare.dto.truck.TruckDTO;
import com.enershare.mapper.truck.TruckMapper;
import com.enershare.model.company.Company;
import com.enershare.model.truck.Truck;
import com.enershare.repository.truck.TruckRepository;
import com.enershare.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    public TruckRepository truckRepository;

    @Autowired
    public TruckMapper truckMapper;

    public List<TruckDTO> getByCurrentUser() {
        String userId = jwtService.getUserId();
        List<Truck> trucks = this.truckRepository.findTrucksByUserId(userId);
        return truckMapper.map(trucks);
    }

}
