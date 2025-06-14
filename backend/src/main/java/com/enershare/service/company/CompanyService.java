package com.enershare.service.company;

import com.enershare.dto.company.CompanyDTO;
import com.enershare.dto.user.UserDTO;
import com.enershare.exception.ApplicationException;
import com.enershare.mapper.company.CompanyMapper;
import com.enershare.model.company.Company;
import com.enershare.repository.company.CompanyRepository;
import com.enershare.repository.truck.TruckRepository;
import com.enershare.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    public CompanyRepository companyRepository;

    @Autowired
    public TruckRepository truckRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public void sync(List<CompanyDTO> companyDTOS) {

        List<String> idList = companyDTOS.stream()
                .map(CompanyDTO::getId)
                .collect(Collectors.toList());

        List<String> existingS1Ids = this.companyRepository.findExistingIds(idList);

        List<CompanyDTO> companiesToSync = companyDTOS.stream()
                .filter(dto -> !existingS1Ids.contains(dto.getId()))
                .collect(Collectors.toList());

        List<Company> companies = companyMapper.toEntities(companiesToSync);

        for (Company model : companies) {
            model.setCreatedOn(Instant.now());
        }

        this.companyRepository.saveAll(companies);
    }

    @Transactional
    @Modifying
    public void syncForce(List<CompanyDTO> companyDTOS) {
        List<Company> companies = this.companyMapper.toEntities(companyDTOS);

        for (Company model : companies) {
            model.setCreatedOn(Instant.now());
        }

        this.companyRepository.saveAll(companies);
    }

    public List<CompanyDTO> getByCurrentUser() {
        String userId = jwtService.getUserId();
        List<String> companyIds = this.truckRepository.findCompanyIdsByUserId(userId);
        List<Company> companies = companyRepository.findByIds(companyIds);
        return companyMapper.map(companies);
    }

}
