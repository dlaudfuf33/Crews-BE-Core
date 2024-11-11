package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.*;
import org.baas.baascore.excaption.BankNotFoundException;
import org.baas.baascore.model.Bank;
import org.baas.baascore.model.Subscribe;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.repository.BankRepository;
import org.baas.baascore.repository.SubscribeRepository;
import org.baas.baascore.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final BankRepository bankRepository;
    private final AccountRepository accountRepository;
    private final CoreTransactionService coreTransactionService;

    public IssueApikeyResponsetDto createSubscription(IssueApikeyRequestDto issueApikeyRequestDto) {

        if (SecurityUtils.isValidBusinessNumber(issueApikeyRequestDto.getBusinessNum())) {
            log.info("사업자등록번호 : {} 인증 성공", issueApikeyRequestDto.getBusinessNum());
        }


        // Bank 엔티티 조회 (통합은행)
        Optional<Bank> foundBank = bankRepository.findById(1L);

        Bank bank;
        if (foundBank.isPresent()) {
            bank = foundBank.get();
        } else {
            throw new BankNotFoundException();
        }

        // 결제
        String companyFinnum = accountRepository.findByAccountNumber(issueApikeyRequestDto.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 계좌 번호 입니다.."))
                .getFintechUseNum();

        coreTransactionService.transfer(new TransferRequestDto(companyFinnum,"777-7777-7777",new BigDecimal(1_000_000),"구독비 결제"));
        // 엔티티 생성
        Subscribe subscribe = Subscribe.createSubscription(
                bank,
                "우리 BaaS API 구독",
                issueApikeyRequestDto.getBusinessNum(),
                issueApikeyRequestDto.getCompanyName()
        );

        // 구독 정보 저장
        subscribeRepository.save(subscribe);
        log.info("{}가(사업자등록번호{}) 구독 시작, api 키 발급 완료", subscribe.getCompanyName(), subscribe.getBusinessNum());

        // 클라이언트에게 반환할 DTO 생성
        return new IssueApikeyResponsetDto(subscribe.getAccessKey(), subscribe.getPlainSecretKey());
    }


    public List<SubcriptionsResponseDto> getSubscriptions(SubcriptionsRequestDto subcriptionsRequestDto) {
        List<Subscribe> subscribes = subscribeRepository
                .findSubscribesByCompanyNameAndBusinessNum(
                        subcriptionsRequestDto.getCompanyName(),
                        subcriptionsRequestDto.getBusinessNum())
                .orElseThrow(() -> new NoSuchElementException("해당 정보로 구독중인 서비스 없음."));

        return subscribes.stream()
                .map(SubcriptionsResponseDto::of)
                .toList();

    }

}
