package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.exception.CustomException;
import org.baas.baascore.exception.ErrorCode;
import org.baas.baascore.model.Account;
import org.baas.baascore.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccountHelper {
    private final AccountRepository accountRepository;

    public Map<String, Account> fetchAndMapAccounts(String fromFintechUseNum, String toAccountNumber) {
        List<Account> accounts = accountRepository.findAccountsForTransfer(fromFintechUseNum, toAccountNumber);

        if (accounts.size() != 2) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND, "출금 또는 입금 계좌를 찾을 수 없습니다.");
        }

        Account fromAccount = accounts.stream()
                .filter(a -> a.getFintechUseNum().equals(fromFintechUseNum))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.WITHDRAW_ACCOUNT_NOT_FOUND));

        Account toAccount = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(toAccountNumber))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.DEPOSIT_ACCOUNT_NOT_FOUND));

        return Map.of("fromAccount", fromAccount, "toAccount", toAccount);
    }
}
