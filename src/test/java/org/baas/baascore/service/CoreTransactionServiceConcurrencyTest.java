package org.baas.baascore.service;

import org.baas.baascore.dto.request.TransferRequest;
import org.baas.baascore.dto.response.TransferResponse;
import org.baas.baascore.model.Account;
import org.baas.baascore.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoreTransactionServiceMockTest {
    private static final Logger log = LoggerFactory.getLogger(CoreTransactionServiceMockTest.class);
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountHelper accountHelper;
    @Mock
    private TransactionHistoryService transactionHistoryService;

    @Mock
    private TransactionExecutionService transactionExecutionService;

    @InjectMocks
    private CoreTransactionService coreTransactionService;

    private final BigDecimal transferAmount = BigDecimal.valueOf(10000);

    private Map<String, Account> mockAccounts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 데이터 초기화
        mockAccounts = new HashMap<>();
        // Mock TransactionExecutionService 설정
        when(transactionExecutionService.execute(any(TransferRequest.class))).thenAnswer(invocation -> {
            TransferRequest request = invocation.getArgument(0);

            // 이체 로직 시뮬레이션
            Account fromAccount = mockAccounts.get(request.getFinUseNum());
            Account toAccount = mockAccounts.get(request.getRecvAccountNum());

            fromAccount.subtractFromBalance(request.getAmt());
            toAccount.addToBalance(request.getAmt());

            return TransferResponse.builder()
                    .historyId(1L)
                    .recvName("테스트")
                    .recvBankcode("001")
                    .recvAccountNum(request.getRecvAccountNum())
                    .amount(request.getAmt())
                    .afterAmt(fromAccount.getBalance())
                    .transactionTime(null)
                    .build();
        });
    }

    @Test
    void testSingleTransfer() throws Exception {
        addMockAccount("mock-finuse-a", BigDecimal.valueOf(300000)); // A 계좌: 잔액 30만 원
        addMockAccount("mock-finuse-b", BigDecimal.ZERO);           // B 계좌: 잔액 0원

        TransferRequest request = new TransferRequest("mock-finuse-a", "mock-finuse-b", transferAmount, "테스트 이체");
        coreTransactionService.transfer(request);

        // 잔액 검증
        Account fromAccount = mockAccounts.get("mock-finuse-a");
        Account toAccount = mockAccounts.get("mock-finuse-b");

        assertEquals(BigDecimal.valueOf(290000), getPrivateBalance(fromAccount), "A 계좌 잔액이 올바르지 않습니다.");
        assertEquals(BigDecimal.valueOf(10000), getPrivateBalance(toAccount), "B 계좌 잔액이 올바르지 않습니다.");
    }

    @Test
    void testConcurrentTransfers() throws Exception {
        int ammountMockA = 12_000_000;
        addMockAccount("mock-finuse-a", BigDecimal.valueOf(ammountMockA)); // A 계좌: 잔액 30만 원
        addMockAccount("mock-finuse-b", BigDecimal.ZERO);           // B 계좌: 잔액 0원

        // 동시성 테스트: A → B로 10번 동시 이체
        int threadCount = 1000;

        Runnable task = () -> {
            try {
                TransferRequest request = new TransferRequest("mock-finuse-a", "mock-finuse-b", transferAmount, "동시성 테스트");
                coreTransactionService.transfer(request);
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        };

        runConcurrentTasks(task, threadCount, 1000);

        // 잔액 검증
        Account fromAccount = mockAccounts.get("mock-finuse-a");
        Account toAccount = mockAccounts.get("mock-finuse-b");
        assertEquals(BigDecimal.valueOf(ammountMockA - (threadCount * 10000)), getPrivateBalance(fromAccount), "A 계좌 잔액이 올바르지 않습니다.");
        assertEquals(BigDecimal.valueOf(10_000 * threadCount), getPrivateBalance(toAccount), "B 계좌 잔액이 올바르지 않습니다.");
    }

    @Test
    void testSequential_BtoCtoD() throws Exception {
        addMockAccount("mock-finuse-b", BigDecimal.valueOf(10000)); // b 계좌: 잔액 30만 원
        addMockAccount("mock-finuse-c", BigDecimal.ZERO);           // c 계좌: 잔액 0원
        addMockAccount("mock-finuse-d", BigDecimal.ZERO);           // d 계좌: 잔액 0원

        // B → C, C → D 순차적 이체 테스트
        TransferRequest request1 = new TransferRequest("mock-finuse-b", "mock-finuse-c", transferAmount, "B → C 이체");
        TransferRequest request2 = new TransferRequest("mock-finuse-c", "mock-finuse-d", transferAmount, "C → D 이체");

        // 첫 번째 이체 (B → C)
        coreTransactionService.transfer(request1);

        // 두 번째 이체 (C → D)
        coreTransactionService.transfer(request2);

        // 잔액 검증
        Account accountB = mockAccounts.get("mock-finuse-b");
        Account accountC = mockAccounts.get("mock-finuse-c");
        Account accountD = mockAccounts.get("mock-finuse-d");

        assertEquals(BigDecimal.ZERO, getPrivateBalance(accountB), "B 계좌 잔액이 올바르지 않습니다.");
        assertEquals(BigDecimal.ZERO, getPrivateBalance(accountC), "C 계좌 잔액이 올바르지 않습니다.");
        assertEquals(BigDecimal.valueOf(10000), getPrivateBalance(accountD), "D 계좌 잔액이 올바르지 않습니다.");
    }


    private void addMockAccount(String fintechUseNum, BigDecimal initialBalance) throws Exception {
        Account account = new Account();
        setPrivateField(account, "fintechUseNum", fintechUseNum); // 핀테크 번호 설정
        setPrivateField(account, "balance", initialBalance);      // 초기 잔액 설정

        mockAccounts.put(fintechUseNum, account); // 생성된 계좌를 Map에 저장

        // Mock Repository에 동작 정의
        when(accountRepository.findByFintechUseNum(fintechUseNum)).thenReturn(Optional.of(account));
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName); // 필드 이름으로 Field 객체 가져오기
        field.setAccessible(true); // private 접근 허용
        field.set(target, value); // 필드 값 설정
    }

    private BigDecimal getPrivateBalance(Account account) throws Exception {
        return (BigDecimal) getPrivateField(account, "balance");
    }

    private Object getPrivateField(Object target, String fieldName) throws Exception {
        if (target == null) {
            throw new IllegalArgumentException("target Object is null. FieldName: " + fieldName);
        }
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private void runConcurrentTasks(Runnable task, int threadCount, int threadPoolSize) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize); // 스레드 풀 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 모든 작업 완료 대기
        CyclicBarrier barrier = new CyclicBarrier(threadCount); // 모든 스레드 동시 시작 대기

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    if (threadPoolSize >= threadCount) {
                        barrier.await(); // 모든 스레드가 준비될 때까지 대기
                    }
                    task.run();      // 작업 실행
                } catch (Exception e) {
                    log.error("Thread Error: {}", Thread.currentThread().getName(), e);
                } finally {
                    latch.countDown(); // 작업 완료
                }
            });
        }


        latch.await(); // 모든 작업 완료 대기
        executorService.shutdown(); // 스레드 풀 종료
    }

    @Test
    void testAccountBalanceRead() throws Exception {
        // 계좌 초기화
        addMockAccount("mock-finuse-b", BigDecimal.valueOf(10000)); // B 계좌: 잔액 1만 원
        addMockAccount("mock-finuse-c", BigDecimal.valueOf(5000));  // C 계좌: 잔액 5천 원
        addMockAccount("mock-finuse-d", BigDecimal.valueOf(0));     // D 계좌: 잔액 0원

        // 단일 스레드에서 계좌 잔액 조회
        Account accountB = accountRepository.findByFintechUseNum("mock-finuse-b").orElseThrow();
        Account accountC = accountRepository.findByFintechUseNum("mock-finuse-c").orElseThrow();
        Account accountD = accountRepository.findByFintechUseNum("mock-finuse-d").orElseThrow();

        // 검증
        assertEquals(BigDecimal.valueOf(10000), accountB.getBalance(), "B 계좌 잔액이 올바르지 않습니다.");
        assertEquals(BigDecimal.valueOf(5000), accountC.getBalance(), "C 계좌 잔액이 올바르지 않습니다.");
        assertEquals(BigDecimal.ZERO, accountD.getBalance(), "D 계좌 잔액이 올바르지 않습니다.");
    }

    @Test
    void testConcurrentAccountBalanceReadPerformance() throws Exception {
        addMockAccount("mock-finuse-b", BigDecimal.valueOf(10000)); // B 계좌: 잔액 1만 원
        int threadCount = 50;
        int testIterations = 100; // 테스트 반복 횟수

        long totalExecutionTimeWithoutLock = 0;
        long totalExecutionTimeWithLock = 0;

        // 락 미적용 테스트 실행
        for (int i = 0; i < testIterations; i++) {
            long startTime = System.currentTimeMillis();

            Runnable task = () -> {
                try {
                    Account account = accountRepository.findByFintechUseNum("mock-finuse-b").orElseThrow();
                    assertEquals(BigDecimal.valueOf(10000), account.getBalance(), "B 계좌 잔액이 올바르지 않습니다.");
                } catch (Exception e) {
                    fail("Exception in read with non-lock task: " + e.getMessage());
                }
            };

            runConcurrentTasks(task, threadCount, 50);

            long endTime = System.currentTimeMillis();
            totalExecutionTimeWithoutLock += (endTime - startTime);
        }


        // 락 적용 테스트 실행
        for (int i = 0; i < testIterations; i++) {
            long startTime = System.currentTimeMillis();

            Runnable task = () -> {
                try {
                    Account account = accountRepository.findByFintechUseNumWithLock("mock-finuse-b").orElseThrow();
                    assertEquals(BigDecimal.valueOf(10000), account.getBalance(), "B 계좌 잔액이 올바르지 않습니다.");
                } catch (Exception e) {
                    fail("Exception in read with lock task: " + e.getMessage());
                }
            };

            runConcurrentTasks(task, threadCount, 50);

            long endTime = System.currentTimeMillis();
            totalExecutionTimeWithLock += (endTime - startTime);
        }

        log.info("non-lock read test result: total runtime {}ms", totalExecutionTimeWithoutLock);
        log.info("lock read test result: total runtime {}ms", totalExecutionTimeWithLock);


        // 성능 비교
        log.info("non-lock avg runtime: {}ms", totalExecutionTimeWithoutLock / testIterations);
        log.info("lock avg runtime: {}ms", totalExecutionTimeWithLock / testIterations);

        assertTrue(totalExecutionTimeWithoutLock < totalExecutionTimeWithLock);
    }



}
