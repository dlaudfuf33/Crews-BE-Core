package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.TransactionDetailRequest;
import org.baas.baascore.dto.TransactionDetailResponse;
import org.baas.baascore.dto.TransactionHistoryDto;
import org.baas.baascore.excaption.AccountNumberNotFoundException;
import org.baas.baascore.excaption.IdentityCodeNotFoundException;
import org.baas.baascore.excaption.MemberNotEqualsException;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.Customer;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.repository.CoreTransactionRepository;
import org.baas.baascore.repository.CustomerRepository;
import org.baas.baascore.repository.TransactionHistoryRepository;
import org.baas.baascore.util.TranType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoreTransactionService {
    private final CoreTransactionRepository coreTransactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public TransactionDetailResponse transactionDetail(TransactionDetailRequest transactionDetailRequest) {
        Account account = accountRepository.findByFintechUseNum(transactionDetailRequest.getFintechUseNum()).orElseThrow(
                AccountNumberNotFoundException::new
        );
        Customer customer = customerRepository.findByIdentityCode(transactionDetailRequest.getIdentityCode()).orElseThrow(
                IdentityCodeNotFoundException::new
        );
        if (!customer.equals(account.getCustomer()))
            throw new MemberNotEqualsException();
        Integer selectPeriod = transactionDetailRequest.getSelectPeriod();
        if(!(selectPeriod == 1 || selectPeriod == 3 || selectPeriod == 6 || selectPeriod == 9))
            throw new IllegalStateException("올바른 기간을 설정해 주세요.");
        LocalDateTime filteredDate = LocalDateTime.now().minusMonths(selectPeriod);
        String transactionType = transactionDetailRequest.getTransactionType();
        String order = transactionDetailRequest.getOrder();
        List<TransactionHistory> list = getTransactionHistories(transactionType, account, filteredDate, order);
        List<TransactionHistoryDto> historyDtoList = list.stream().map(TransactionHistoryDto::from).toList();
        return TransactionDetailResponse.builder().tranList(historyDtoList).build();
    }

    private List<TransactionHistory> getTransactionHistories(String transactionType, Account account, LocalDateTime filteredDate, String order) {
        List<TransactionHistory> list;
        final String ALL = "ALL";
        final String DESC = "DESC";
        final String DEPOSIT = "DEPOSIT";
        final String WITHDRAW = "WITHDRAW";
        if(transactionType.equalsIgnoreCase(ALL)) {
            list = transactionHistoryRepository.findTransactionHistoryAllTranType(account, filteredDate);
            if(order.equalsIgnoreCase(DESC))
                list.sort((o1, o2) ->
                        o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        }
        else if (transactionType.equalsIgnoreCase(DEPOSIT) || transactionType.equalsIgnoreCase(WITHDRAW)){
            list = transactionHistoryRepository.findTransactionHistorySelectedTranType(account, filteredDate, TranType.valueOf(transactionType.toUpperCase()));
            if(order.equalsIgnoreCase(DESC))
                list.sort((o1, o2) ->
                        o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        }
        else {
            throw new IllegalStateException("잘못된 TransactionType을 입력하셨습니다.");
        }
        return list;
    }
}


