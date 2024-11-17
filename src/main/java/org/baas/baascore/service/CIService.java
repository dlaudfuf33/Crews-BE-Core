package org.baas.baascore.service;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.baas.baascore.dto.AccountIssuedRequest;
import org.baas.baascore.dto.AccountIssuedResponse;
import org.baas.baascore.dto.CIRequest;
import org.baas.baascore.dto.IdentityRequest;
import org.baas.baascore.dto.TransferRequestDto;
import org.baas.baascore.model.Customer;
import org.baas.baascore.repository.CustomerRepository;
import org.baas.baascore.util.AccountType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CIService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;
    private final CoreTransactionService coreTransactionService;

    @Transactional
    public AccountIssuedResponse ciSave(CIRequest ciRequest){
        Customer customer = customerRepository.findByCi(ciRequest.getCi()).orElse(null);
        if(customer == null) {
            Customer makeCustomer = Customer.builder()
                .name(ciRequest.getName())
                .email(ciRequest.getEmail())
                .phoneNum(ciRequest.getPhone())
                .ci(ciRequest.getCi())
                .build();
            customerRepository.save(makeCustomer);
        }
        AccountIssuedRequest accountIssuedRequest = AccountIssuedRequest.builder().ci(ciRequest.getCi()).build();
        AccountIssuedResponse accountIssuedResponse = accountService.accountIssued(accountIssuedRequest,
            AccountType.PERSONAL);
        TransferRequestDto transferRequestDto = new TransferRequestDto("9df5bf03-cf53-4a48-8c6d-03b8c36f5783",accountIssuedResponse.getAccountNumber(),
            BigDecimal.valueOf(500000),"초기 지원금");
        coreTransactionService.transfer(transferRequestDto);
        accountIssuedResponse.setBalance(BigDecimal.valueOf(500000));
        return accountIssuedResponse;
    }
}
