package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.TransferRequestDto;
import org.baas.baascore.dto.TransferResponseDto;
import org.baas.baascore.dto.TransferStatesRequestDto;
import org.baas.baascore.dto.TransferStatesResponseDto;
import org.baas.baascore.service.CoreTransactionService;
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
    public TransferResponseDto withdraw(@RequestBody TransferRequestDto transferRequestDto) {
        return coreTransactionService.transfer(transferRequestDto);
    }

    @PostMapping("/states")
    public TransferStatesResponseDto trxStateCheck(@RequestBody TransferStatesRequestDto transferStatesRequestDto) {
        return coreTransactionService.getTransactionStatus(transferStatesRequestDto);
    }
}
