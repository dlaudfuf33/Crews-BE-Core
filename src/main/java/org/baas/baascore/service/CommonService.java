package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.request.AccountIssuedRequest;
import org.baas.baascore.dto.request.BalanceInfoRequest;
import org.baas.baascore.dto.request.CIRequest;
import org.baas.baascore.dto.request.TransferRequest;
import org.baas.baascore.dto.response.AccountIssuedResponse;
import org.baas.baascore.dto.response.BalanceInfoResponse;
import org.baas.baascore.dto.response.ProductAllResponse;
import org.baas.baascore.dto.response.ProductResponse;
import org.baas.baascore.exception.CustomException;
import org.baas.baascore.exception.ErrorCode;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.Customer;
import org.baas.baascore.model.Product;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.repository.CustomerRepository;
import org.baas.baascore.repository.ProductRepository;
import org.baas.baascore.util.AccountType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;
    private final CoreTransactionService coreTransactionService;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private static String WOORI_BANK;

    @Value("${bank.woori}")
    public void setWooriBank(String wooriBank) {
        WOORI_BANK = wooriBank;
    }


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
        TransferRequest transferRequest = new TransferRequest(WOORI_BANK,accountIssuedResponse.getAccountNumber(),
            BigDecimal.valueOf(500000),"초기 지원금");
        coreTransactionService.transfer(transferRequest);
        accountIssuedResponse.setBalance(BigDecimal.valueOf(500000));
        return accountIssuedResponse;
    }

    public ProductAllResponse getAllProductInfo() {
        List<Product> productList = productRepository.findAll();
        if(productList.isEmpty()) return ProductAllResponse.builder().build();
        return ProductAllResponse.builder().products(productList.stream().map(ProductResponse::from).toList()).build();
    }

    public BalanceInfoResponse getBalanceInfo(BalanceInfoRequest balanceInfoRequest) {
        Account account = accountRepository.findByFintechUseNum(balanceInfoRequest.getFintecUseNum()).orElseThrow(
                () -> new CustomException(ErrorCode.ACCOUNTNUMBER_NOT_FOUND)
        );

        Account recvAccount = accountRepository.findByFintechUseNum(balanceInfoRequest.getRecvFintecUseNum()).orElseThrow(
                () -> new CustomException(ErrorCode.ACCOUNTNUMBER_NOT_FOUND)
        );

        return BalanceInfoResponse.builder().accountNumber(account.getAccountNumber()).afterBalanceAmt(account.getBalance())
                .recvAccountNumber(recvAccount.getAccountNumber()).recvAfterBalanceAmt(recvAccount.getBalance())
                .build();

    }
}
