package org.baas.baascore.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.request.*;
import org.baas.baascore.dto.response.*;
import org.baas.baascore.service.AccountService;
import org.baas.baascore.util.AccountType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountController {
    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<AccountIssuedResponse> accountIssued(@RequestBody AccountIssuedRequest accountIssuedRequest) {
        return ResponseEntity.ok().body(accountService.accountIssued(accountIssuedRequest, AccountType.CREW));

    }

    @DeleteMapping
    public ResponseEntity<AccountDeleteResponse> accountDelete(@RequestBody AccountDeleteRequest accountDeleteRequest) {
        return ResponseEntity.ok().body(accountService.accountDelete(accountDeleteRequest));

    }

    @PostMapping("/info")
    public ResponseEntity<AccountInfoResponse> accountInfo(@RequestBody AccountInfoRequest accountInfoRequest) {
        return ResponseEntity.ok().body(accountService.accountInfo(accountInfoRequest));

    }

    @PostMapping("/one")
    public ResponseEntity<AccountOneResponse> accountInfoOne(@RequestBody CommonRequest commonRequest) {
        return ResponseEntity.ok().body(accountService.accountInfoOne(commonRequest));

    }


    @PostMapping("/fin-num")
    public ResponseEntity<FintechNumResponse> fintechNum(@RequestBody FintechNumRequest fintechNumRequest) {
        return ResponseEntity.ok().body(accountService.fintechNum(fintechNumRequest));
    }

    @PostMapping("/fin-nums")
    public ResponseEntity<MultiFintechNumsResponse> multiFintechNum(@RequestBody MultiFintechNumRequest multiFintechNumRequest) {
        return ResponseEntity.ok().body(accountService.multiFintechNum(multiFintechNumRequest));
    }


    @PostMapping("/info/init")
    public ResponseEntity<AccountsInfoResponse> getAccountInfo(@RequestBody MemberInitRequest memberRequestDtoDto) {
        return ResponseEntity.ok(accountService.findAccountInfo(memberRequestDtoDto));
    }

    @PostMapping("/info/balance")
    public ResponseEntity<List<FintechBalancePairResponse>> getAccountBalance(@RequestBody BalanceLoadRequest balanceLoadRequest) {
        return ResponseEntity.ok(accountService.getBalance(balanceLoadRequest));
    }

    @PostMapping("/info/date")
    public ResponseEntity<TransactionDetailResponse> getAccountInfoOfDate(@RequestBody AccountInfoOfDate accountInfoOfDate) {
        return ResponseEntity.ok().body(accountService.getAccountInfoOfDate(accountInfoOfDate));
    }
}


