package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.*;
import org.baas.baascore.excaption.CustomException;
import org.baas.baascore.service.CoreTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/transfer")
public class CoreTransactionController {
    private final CoreTransactionService coreTransactionService;

    @PostMapping
    public ResponseEntity<TransferResponseDto> withdraw(@RequestBody TransferRequestDto transferRequestDto) {
        try {
            TransferResponseDto responseDto = coreTransactionService.transfer(transferRequestDto);
            return ResponseEntity.ok(responseDto); // 성공 시 200 OK와 응답 데이터 반환
        } catch (CustomException e) {
            // CustomException 발생 시, 예외의 상태 코드와 메시지 반환
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(null);
        } catch (Exception e) {
            // 예상치 못한 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/states")
    public TransferStatesResponseDto trxStateCheck(@RequestBody TransferStatesRequestDto transferStatesRequestDto) {
        return coreTransactionService.getTransactionStatus(transferStatesRequestDto);
    }

    @PostMapping("/details")
    public ResponseEntity<TransactionDetailResponse> transactionDetail(
            @RequestBody TransactionDetailRequest transactionDetailRequest) {
        return ResponseEntity.ok().body(coreTransactionService.transactionDetail(transactionDetailRequest));
    }
}
