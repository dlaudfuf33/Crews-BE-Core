package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.request.*;
import org.baas.baascore.dto.response.*;
import org.baas.baascore.exception.CustomException;
import org.baas.baascore.exception.ErrorCode;
import org.baas.baascore.model.*;
import org.baas.baascore.repository.*;
import org.baas.baascore.util.AccountType;
import org.baas.baascore.util.CurrencyType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountService {
    private static final Random RANDOM = new Random(); // Random 객체를 static 멤버 변수로 선언하여 재사용
    private static final String ONLY_BANK_NUM = "1002"; // 우리은행 계좌번호 앞자리 4글자

    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final ProductRepository productRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;


    public AccountIssuedResponse accountIssued(AccountIssuedRequest accountIssuedRequest, AccountType accountTypeCrew) {
        Customer customer = customerRepository.findByCi(accountIssuedRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );
        String fintechUseNum = UUID.randomUUID().toString();
        Bank bank = bankRepository.findByBankCode("020").orElseThrow( //"020 - 우리은행 은행코드
                () -> new CustomException(ErrorCode.BANK_NOT_FOUND)
        );
        Product product = productRepository.findById(accountIssuedRequest.getProductId()).orElseThrow(
                () -> new CustomException(ErrorCode.BANK_NOT_FOUND)
        );
        String accountNumber = getAccountNumber();
        BigDecimal balance = BigDecimal.ZERO;
        CurrencyType currencyType = CurrencyType.KRW;
        Account account = Account.builder().customer(customer).bank(bank).product(product).accountNumber(accountNumber).balance(balance)
                .currencyType(currencyType).accountType(accountTypeCrew).fintechUseNum(fintechUseNum).build();
        Account savedAccount = accountRepository.save(account);
        log.info("생성된 계좌번호 : {}, 이름 {}, 식별자번호 {}", accountNumber, customer.getName(), customer.getCi());
        return AccountIssuedResponse.from(savedAccount);

    }

    public AccountDeleteResponse accountDelete(AccountDeleteRequest accountDeleteRequest) {
        Customer customer = customerRepository.findByCi(accountDeleteRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );
        Account account = accountRepository.findByFintechUseNum(accountDeleteRequest.getFintechUseNum()).orElseThrow(
                () -> new CustomException(ErrorCode.FINTECHCODE_NOT_FOUND)
        );
        if (!customer.equals(account.getCustomer()))
            throw new CustomException(ErrorCode.MEMBER_NOT_EQUALS);
        else if (!account.getBank().getBankCode().equals("020"))
            throw new IllegalStateException("우리은행의 계좌가 아닙니다.");
        else if (account.getBalance().compareTo(BigDecimal.ZERO) != 0)
            throw new CustomException(ErrorCode.BALANCE_NOT_ZERO);
        account.accountDeleted(true);
        return AccountDeleteResponse.from(account.isDeleted());
    }

    public AccountInfoResponse accountInfo(AccountInfoRequest accountInfoRequest) {
        Customer customer = customerRepository.findByCi(accountInfoRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );
        List<Account> accountList = accountRepository.findByCustomerAndIsDeletedAndAccountType(customer, false, AccountType.PERSONAL);
        List<Card> cardList = cardRepository.findByCustomerAndCardStatusTrueAndExpiredAtGreaterThan(customer, LocalDateTime.now());


        List<AccountIssuedResponse> changedAccountList = accountList.stream().map(AccountIssuedResponse::from).toList();
        List<CardListResponse> chagedCardList = cardList.stream().map(CardListResponse::from).toList();
        return AccountInfoResponse.builder().accountList(changedAccountList).cardList(chagedCardList).build();

    }

    public AccountOneResponse accountInfoOne(CommonRequest commonRequest) {
        Customer customer = customerRepository.findByCi(commonRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );
        Account account = accountRepository.findByFintechUseNum(commonRequest.getFintechUseNum()).orElseThrow(
                () -> new CustomException(ErrorCode.FINTECHCODE_NOT_FOUND)
        );
        if (!customer.equals(account.getCustomer()))
            throw new CustomException(ErrorCode.MEMBER_NOT_EQUALS);
        log.info("{}({})의 account({})를 조회했습니다.", customer.getName(), customer.getCi(), account.getAccountNumber());
        return AccountOneResponse.from(account);
    }

    public FintechNumResponse fintechNum(FintechNumRequest fintechNumRequest) {
        Customer customer = customerRepository.findByCi(fintechNumRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );

        String accountNumber = fintechNumRequest.getAccountNumber();
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new CustomException(ErrorCode.ACCOUNTNUMBER_NOT_FOUND)
        );
        if (!customer.equals(account.getCustomer()))
            throw new CustomException(ErrorCode.MEMBER_NOT_EQUALS);

        return FintechNumResponse.from(account);

    }

    public MultiFintechNumsResponse multiFintechNum(MultiFintechNumRequest multiFintechNumRequest) {
        String accountNumbersStr = String.join(", ", multiFintechNumRequest.getAccountNumbers());
        log.info("핀테크 번호 요청 처리 중: CI={}, 계좌 번호들={}",
                multiFintechNumRequest.getCi(),
                accountNumbersStr);

        // CI로 고객 조회
        Customer customer = customerRepository.findByCi(multiFintechNumRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );

        List<String> accountNumberList = multiFintechNumRequest.getAccountNumbers();
        List<Account> accounts = accountRepository.findByAccountNumbers(accountNumberList);

        if (accounts.isEmpty()) {
            log.warn("제공된 계좌 번호들에 해당하는 계좌를 찾을 수 없습니다: {}", accountNumberList);
            throw new CustomException(ErrorCode.ACCOUNTNUMBER_NOT_FOUND);
        }

        // 모든 계좌가 동일한 고객에게 속하는지 확인
        boolean allBelongToCustomer = accounts.stream()
                .allMatch(account -> customer.equals(account.getCustomer()));

        if (!allBelongToCustomer) {
            log.warn("일부 계좌가 요청한 고객과 일치하지 않습니다.");
            throw new CustomException(ErrorCode.MEMBER_NOT_EQUALS);
        }

        // MultiFintechNumResponse 리스트 생성 및 인덱스 부여 (안전한 방식)
        List<MultiFintechNumResponse> fintechResponses = IntStream.range(0, accounts.size())
                .mapToObj(i -> MultiFintechNumResponse.of(accounts.get(i), i + 1)) // 인덱스 1부터 시작
                .toList();

        MultiFintechNumsResponse response = new MultiFintechNumsResponse(fintechResponses.size(), fintechResponses);
        log.info("핀테크 번호 응답 생성 완료: {}", response);
        return response;
    }

    @Retryable
    private String getAccountNumber() {
        String accountNumber = createAccountNumber();
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isEmpty()) {
            return accountNumber;
        }
        throw new CustomException(ErrorCode.ACCOUNTNUMBER_DUPLICATED);
    }

    public static String createAccountNumber() {
        StringBuilder randomNum = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            int createNum = RANDOM.nextInt(10); // 0~9 사이의 랜덤 숫자 생성
            randomNum.append(createNum); // StringBuilder에 추가
        }
        return ONLY_BANK_NUM + randomNum.toString(); // 은행 코드와 랜덤 번호 조합하여 반환
    }

    // 고객 ID로 모든 계좌 조회
    public List<Account> findAccountsByCustomerId(Long customerId) {
        // 고객 ID로 계좌 조회 후 반환
        return accountRepository.findByCustomerId(customerId);
    }

    public AccountsInfoResponse findAccountInfo(MemberInitRequest memberInitRequest) {
        log.info("{}   {} ", memberInitRequest, memberInitRequest.getCi());
        Customer customer = customerRepository.findByCi(memberInitRequest.getCi())
                .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND));

        AtomicInteger index = new AtomicInteger(1);
        List<AccountInitResponse> accountInfoList = accountRepository.findByCustomerId(customer.getId()).stream()
                .map(account -> AccountInitResponse.from(account, index.getAndIncrement()))
                .toList();

        return new AccountsInfoResponse(accountInfoList.size(), accountInfoList);

    }

    public List<FintechBalancePairResponse> getBalance(BalanceLoadRequest balanceLoadRequest) {
        return accountRepository.findBalancesByFintechNumbers(balanceLoadRequest.getFintechNum());
    }

    public TransactionDetailResponse getAccountInfoOfDate(AccountInfoOfDate accountInfoOfDate) {

        Account account = accountRepository.findByFintechUseNum(accountInfoOfDate.getFintechUseNum()).orElseThrow(
            () -> new CustomException(ErrorCode.ACCOUNTNUMBER_NOT_FOUND)
        );
        Customer customer = customerRepository.findByCi(accountInfoOfDate.getCi()).orElseThrow(
            () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );
        if (!customer.equals(account.getCustomer()) && account.getAccountType().equals(AccountType.PERSONAL)){
            throw new CustomException(ErrorCode.MEMBER_NOT_EQUALS);
        }
        List<TransactionHistory> list = transactionHistoryRepository.findTransactionHistoryYearAndMonth(
            account, accountInfoOfDate.getYear(), accountInfoOfDate.getMonth(), accountInfoOfDate.getTranType());

        List<TransactionHistoryResponse> historyDtoList = list.stream().map(TransactionHistoryResponse::from).toList();
        return TransactionDetailResponse.builder().tranList(historyDtoList)
            .accountNumber(account.getAccountNumber())
            .productName(account.getProduct().getProductName())
            .balance(account.getBalance())
            .bankCode(account.getBank().getBankCode())
            .bankName(account.getBank().getBankName())
            .build();
    }
}
