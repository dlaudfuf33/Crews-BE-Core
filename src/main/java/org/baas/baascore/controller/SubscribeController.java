package org.baas.baascore.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.SubcriptionsRequestDto;
import org.baas.baascore.dto.SubcriptionsResponseDto;
import org.baas.baascore.excaption.CustomException;
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
        String accessKey = request.getHeader("X-ACCESS-KEY");
        try {
            subscribeService.escapeFromSubscriptions(accessKey);
            return ResponseEntity.ok("구독이 성공적으로 취소되었습니다.");
        } catch (CustomException ex) {
            // `CustomException` 발생 시, 해당 예외의 상태 코드와 메시지를 반환
            return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(ex.getMessage());
        } catch (Exception ex) {
            // 예상치 못한 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }


    @PostMapping("/details")
    public ResponseEntity<List<SubcriptionsResponseDto>> getSubcribeLise(@RequestBody SubcriptionsRequestDto
                                                                                 subcriptionsRequestDto) {
        return ResponseEntity.ok(subscribeService.getSubscriptions(subcriptionsRequestDto));
    }
}
