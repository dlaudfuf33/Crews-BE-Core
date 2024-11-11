package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baas.baascore.dto.SubcriptionsRequestDto;
import org.baas.baascore.dto.SubcriptionsResponseDto;
import org.baas.baascore.dto.SubscribeRequestDto;
import org.baas.baascore.dto.SubscribeResponseDto;
import org.baas.baascore.excaption.BankNotFoundException;
import org.baas.baascore.model.Bank;
import org.baas.baascore.model.Subscribe;
import org.baas.baascore.repository.BankRepository;
import org.baas.baascore.repository.SubscribeRepository;
import org.baas.baascore.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final BankRepository bankRepository;

    public SubscribeResponseDto createSubscription(SubscribeRequestDto subscribeRequestDto) {

        if (SecurityUtils.isValidBusinessNumber(subscribeRequestDto.getBusinessNum())) {
            log.info("사업자등록번호 : {} 인증 성공", subscribeRequestDto.getBusinessNum());
        }

        // Bank 엔티티 조회
        Optional<Bank> foundBank = bankRepository.findById(subscribeRequestDto.getBankId());

        Bank bank;
        if (foundBank.isPresent()) {
            bank = foundBank.get();
        } else {
            throw new BankNotFoundException();
        }
        // 엔티티 생성
        Subscribe subscribe = Subscribe.createSubscription(
                bank,
                subscribeRequestDto.getProductName(),
                subscribeRequestDto.getBusinessNum(),
                subscribeRequestDto.getCompanyName()
        );

        // 구독 정보 저장
        subscribeRepository.save(subscribe);
        log.info("{}가(사업자등록번호{}) 구독 시작, api 키 발급 완료", subscribe.getCompanyName(), subscribe.getBusinessNum());

        // 클라이언트에게 반환할 DTO 생성
        return new SubscribeResponseDto(subscribe.getAccessKey(), subscribe.getPlainSecretKey());
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
