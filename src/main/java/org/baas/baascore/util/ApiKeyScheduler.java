package org.baas.baascore.util;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.service.SubscribeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ApiKeyScheduler {

    private final SubscribeService subscribeService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void maskExpiredApiKeys() {
        subscribeService.maskExpiredApiKeys();
    }
}
