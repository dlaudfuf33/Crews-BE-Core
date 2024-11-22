package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.request.TransactionDetailRequest;
import org.baas.baascore.dto.request.TransferRequest;
import org.baas.baascore.dto.request.TransferStatesRequest;
import org.baas.baascore.dto.response.ApiResponse;
import org.baas.baascore.dto.response.TransactionDetailResponse;
import org.baas.baascore.dto.response.TransferResponse;
import org.baas.baascore.dto.response.TransferStatesResponse;
import org.baas.baascore.exception.CustomException;
import org.baas.baascore.exception.ErrorResponse;
import org.baas.baascore.service.CoreTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/transfer")
public class CoreTransactionController {
    private final CoreTransactionService coreTransactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> withdraw(@RequestBody TransferRequest transferRequest) {
        try {
            TransferResponse responseDto = coreTransactionService.transfer(transferRequest);
            return ResponseEntity.ok(ApiResponse.<TransferResponse>builder()
                    .data(responseDto)
                    .success(true)
                    .build()); // 성공 시 200 OK와 응답 데이터 반환

        } catch (CustomException e) {
            // CustomException 발생 시, 예외의 상태 코드와 메시지 반환
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(e.getErrorCode().name())
                    .message(e.getErrorCode().getMessage())
                    .details(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiResponse.<TransferResponse>builder()
                    .error(errorResponse)
                    .success(false)
                    .build());
        } catch (Exception e) {
            // 예상치 못한 예외 처리
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode("INTERNAL_SERVER_ERROR")
                    .message("서버 내부 오류가 발생했습니다.")
                    .details(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<TransferResponse>builder()
                    .error(errorResponse)
                    .success(false)
                    .build());
        }
    }


    @PostMapping("/states")
    public TransferStatesResponse trxStateCheck(@RequestBody TransferStatesRequest transferStatesRequest) {
        return coreTransactionService.getTransactionStatus(transferStatesRequest);
    }

    @PostMapping("/details")
    public ResponseEntity<TransactionDetailResponse> transactionDetail(
            @RequestBody TransactionDetailRequest transactionDetailRequest) {
        return ResponseEntity.ok().body(coreTransactionService.transactionDetail(transactionDetailRequest));
    }
}
