package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.SubcriptionsRequestDto;
import org.baas.baascore.dto.SubcriptionsResponseDto;
import org.baas.baascore.service.SubscribeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/subscriptions")
public class SubscribeController {
    private final SubscribeService subscribeService;

    @PostMapping("/states")
    public ResponseEntity<List<SubcriptionsResponseDto>> getSubcriptionsstatement(@RequestBody SubcriptionsRequestDto subcriptionsRequestDto) {
        return ResponseEntity.ok(subscribeService.getSubscriptions(subcriptionsRequestDto));
    }

}
