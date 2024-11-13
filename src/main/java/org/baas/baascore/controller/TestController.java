package org.baas.baascore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test-api")
@Slf4j
public class TestController {
    private  final CustomerService customerService;
    /**
     * 아무튼 테스트하는 api
     *
     * @return
     */
    @PostMapping("/nonblock")
    public String testApiBlock(@RequestBody Object o) {
        log.info("서비스 서버가 {}", o);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "여기는 코어서버라고 알림 (블록)";
    }

    /**
     * 아무튼 테스트하는 api
     *
     * @return
     */
    @PostMapping("/block")
    public String testApiNonBlock(@RequestBody Object o) {
        log.info("서비스 서버가 {}", o);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "여기는 코어서버라고 알림 (논블록)";
    }
}