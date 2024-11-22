package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.request.AccountIssuedRequest;
import org.baas.baascore.dto.request.CIRequest;
import org.baas.baascore.dto.request.TransferRequest;
import org.baas.baascore.dto.response.AccountIssuedResponse;
import org.baas.baascore.model.Customer;
import org.baas.baascore.repository.CustomerRepository;
import org.baas.baascore.util.AccountType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
        TransferRequest transferRequest = new TransferRequest("9df5bf03-cf53-4a48-8c6d-03b8c36f5783",accountIssuedResponse.getAccountNumber(),
            BigDecimal.valueOf(500000),"초기 지원금");
        coreTransactionService.transfer(transferRequest);
        accountIssuedResponse.setBalance(BigDecimal.valueOf(500000));
        return accountIssuedResponse;
    }
}
