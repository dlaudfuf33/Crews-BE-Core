package org.baas.baascore.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.SubcriptionsRequestDto;
import org.baas.baascore.dto.SubcriptionsResponseDto;
import org.baas.baascore.service.SubscribeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping
    public ResponseEntity<String> escapeFromSubscriptions(HttpServletRequest request) {
        try {
            String accessKey = request.getHeader("X-ACCESS-KEY");
            int result = subscribeService.escapeFromSubscriptions(accessKey);

            if (result == 1) {
                // 구독 취소 성공
                return ResponseEntity.ok("구독이 성공적으로 취소되었습니다.");
            } else if (result == 2) {
                // 이미 구독 취소됨
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 취소된 구독입니다.");
            } else {
                // 구독 취소 실패
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("구독 취소에 실패했습니다. 요청을 다시 확인하세요.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구독 취소 요청 중 오류가 발생했습니다.");
        }
    }
    @PostMapping("/details")
    public ResponseEntity<List<SubcriptionsResponseDto>> getSubcribeLise(@RequestBody SubcriptionsRequestDto subcriptionsRequestDto){
        return ResponseEntity.ok(subscribeService.getSubscriptions(subcriptionsRequestDto));
    }
}
