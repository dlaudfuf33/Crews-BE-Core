package org.baas.baascore.service;

import org.baas.baascore.dto.TransferRequestDto;
import org.baas.baascore.dto.TransferResponseDto;
import org.baas.baascore.model.Account;
import org.baas.baascore.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoreTransactionServiceConcurrencyTest {
    private static final Logger log = LoggerFactory.getLogger(CoreTransactionServiceConcurrencyTest.class);

    @Autowired
    private CoreTransactionService coreTransactionService;

    private final String finUseNum = "FNUM001";
    private final String recvAccountNum = "330-7890-1234";
    private final BigDecimal amt = BigDecimal.valueOf(10000); // 이체할 금액 1만원
    private final String description = "설명";
    @Autowired
    private AccountRepository accountRepository;


    @BeforeEach
    void setUp() {
        // 출금 계좌 초기화 (잔액 100만 원)
        Account fromAccount = accountRepository.findByFintechUseNum(finUseNum).orElseThrow();
        BigDecimal initialFromBalance = BigDecimal.valueOf(1000000).subtract(fromAccount.getBalance());
        fromAccount.addToBalance(initialFromBalance);  // 현재 잔액에서 100만 원으로 초기화
        accountRepository.save(fromAccount);

        // 입금 계좌 초기화 (잔액 0원)
        Account toAccount = accountRepository.findByAccountNumber(recvAccountNum).orElseThrow();
        BigDecimal initialToBalance = BigDecimal.ZERO.subtract(toAccount.getBalance());
        toAccount.addToBalance(initialToBalance);  // 현재 잔액에서 0원으로 초기화
        accountRepository.save(toAccount);
    }


    @Test
    void testConcurrentTransferExecution() {
        int threadCount = 101; // 101번의 요청 실행
        ExecutorService executorService = Executors.newFixedThreadPool(100); // 50개의 스레드 풀 사용
        CountDownLatch latch = new CountDownLatch(threadCount);  // 모든 스레드 종료 대기

        List<TransferResponseDto> responses = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    TransferRequestDto requestDto = new TransferRequestDto(finUseNum, recvAccountNum, amt, description);
                    TransferResponseDto response = coreTransactionService.transfer(requestDto);
                    synchronized (responses) {
                        responses.add(response);
                    }
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        try {
            boolean completed = latch.await(10, TimeUnit.SECONDS);
            if (!completed) {
                log.warn("일부 스레드가 완료되지 않았습니다. 타임아웃 발생");
            }
        } catch (InterruptedException e) {
            log.error("대기 중 인터럽트 발생", e);
            Thread.currentThread().interrupt(); // 인터럽트 상태 복구
        }

// Executor 서비스 종료
        executorService.shutdown();

        // 검증 1: 성공 거래의 개수가 99건인지 확인
        assertEquals(100, responses.size(), "성공 거래의 수는 100여야 합니다.");

        // 검증 2: 실패가 정확히 1건인지 확인
        assertEquals(1, exceptions.size(), "실패 거래는 정확히 1건이어야 합니다.");

        // 검증 3: 잔액이 음수가 아닌지 확인
        BigDecimal finalBalance = accountRepository.findByFintechUseNum(finUseNum).orElseThrow().getBalance();
        assertTrue(finalBalance.compareTo(BigDecimal.ZERO) >= 0, "최종 잔액이 음수가 아닙니다.");

        System.out.println("동시성 테스트 완료: 성공 거래 수 = " + responses.size() + ", 실패 거래 수 = " + exceptions.size());
    }


}
