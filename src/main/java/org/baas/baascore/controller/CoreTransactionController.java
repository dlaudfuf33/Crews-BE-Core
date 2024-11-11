package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.TransactionDetailRequest;
import org.baas.baascore.dto.TransactionDetailResponse;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.service.CoreTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/transfer")
public class CoreTransactionController {
    private final CoreTransactionService coreTransactionService;

    @PostMapping("/details")
    public ResponseEntity<TransactionDetailResponse> transactionDetail(
            @RequestBody  TransactionDetailRequest transactionDetailRequest){
        return ResponseEntity.ok().body(coreTransactionService.transactionDetail(transactionDetailRequest));
    }
}
