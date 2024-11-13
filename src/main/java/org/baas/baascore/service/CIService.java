package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.CIRequest;
import org.baas.baascore.dto.IdentityRequest;
import org.baas.baascore.model.Customer;
import org.baas.baascore.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CIService {

    private final CustomerRepository customerRepository;

    @Transactional
    public String ciSave(CIRequest ciRequest){
        Customer customer = customerRepository.findByCi(ciRequest.getCi()).orElse(null);
        if(customer != null)
            return "ok";
        Customer makeCustomer = Customer.builder().name(ciRequest.getName()).email(ciRequest.getEmail()).phoneNum(ciRequest.getPhone())
                .ci(ciRequest.getCi()).build();
        customerRepository.save(makeCustomer);
        return "ok";
    }
}
