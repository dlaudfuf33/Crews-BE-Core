package org.baas.baascore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.*;
import org.baas.baascore.excaption.CustomException;
import org.baas.baascore.excaption.ErrorCode;
import org.baas.baascore.model.Bank;
import org.baas.baascore.model.Subscribe;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.repository.BankRepository;
import org.baas.baascore.repository.SubscribeRepository;
import org.baas.baascore.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
            throw new CustomException(ErrorCode.BANK_NOT_FOUND);
        }

        // 결제
        String companyFinnum = accountRepository.findByAccountNumber(issueApikeyRequestDto.getAccountNumber()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_ACCOUNTNUMBER)).getFintechUseNum();
        coreTransactionService.transfer(new TransferRequestDto(companyFinnum, "777-7777-7777", new BigDecimal(1_000_000), "구독비 결제"));
        // 엔티티 생성
        Subscribe subscribe = Subscribe.createSubscription(bank, "우리 BaaS API 구독", issueApikeyRequestDto.getBusinessNum(), issueApikeyRequestDto.getCompanyName());

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
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        return subscribes.stream().map(SubcriptionsResponseDto::of).toList();

    }

    @Transactional
    public void escapeFromSubscriptions(String accessKey) {
        try {
            Subscribe target = subscribeRepository.findByAccessKey(accessKey).orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
            if (!target.isSubscribed()) {
                log.info("{} 는 이미 취소된 구독입니다.", target.getId());
                throw new CustomException(ErrorCode.ALREADY_CANCELED);
            }
            target.setSubscribed(false); // 구독 취소
            log.info("구독이 성공적으로 취소되었습니다. Access Key: {}", accessKey);
        } catch (Exception e) {
            // 그 외 일반적인 예외 처리
            log.error("구독 취소 중 알 수 없는 오류 발생 - Access Key: {}. 에러 메시지: {}", accessKey, e.getMessage(), e);
            throw new CustomException(ErrorCode.UNSUBSCRIBE_FAILED);
        }
    }

    public void maskExpiredApiKeys() {
        List<Subscribe> expiredSubscriptions = subscribeRepository.findAllByExpireDateBefore(LocalDateTime.now());
        for (Subscribe subscribe : expiredSubscriptions) {
            subscribe.setAccessKey(maskApiKey(subscribe.getAccessKey()));
        }
        subscribeRepository.saveAll(expiredSubscriptions);
    }

    private String maskApiKey(String apiKey) {
        if (apiKey.length() > 4) {
            return "****" + apiKey.substring(apiKey.length() - 4);
        }
        return "****";
    }
}
