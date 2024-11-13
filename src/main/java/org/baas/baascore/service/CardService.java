package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.CardIssuedResponse;
import org.baas.baascore.dto.CardReissuedRequest;
import org.baas.baascore.dto.CommonRequest;
import org.baas.baascore.excaption.CustomException;
import org.baas.baascore.excaption.ErrorCode;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.Card;
import org.baas.baascore.model.Customer;
import org.baas.baascore.repository.AccountRepository;
import org.baas.baascore.repository.CardRepository;
import org.baas.baascore.repository.CustomerRepository;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private static final Random RANDOM = new Random();
    private static final String CARDNAME = "우리트래블카드";
    private static final String CARD_PREFIX = "356820";
    private static final String BANK_CODE = "020";

    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public CardIssuedResponse cardIssued(CommonRequest commonRequest) {
        Customer customer = customerRepository.findByCi(commonRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );
        Account account = accountRepository.findByFintechUseNum(commonRequest.getFintechUseNum()).orElseThrow(
                () -> new CustomException(ErrorCode.FINTECHCODE_NOT_FOUND)
        );
        if (!account.getBank().getBankCode().equals(BANK_CODE)) {
            throw new CustomException(ErrorCode.WRONG_BANK);
        }
        List<Card> cardList = cardRepository.findByCustomerAndCardStatusTrueAndExpiredAtGreaterThan(customer, LocalDateTime.now());
        if (!cardList.isEmpty())
            throw new CustomException(ErrorCode.CARDNUMBER_DUPLICATED);

        Card card = createCard(account, customer);
        Card savedCard = cardRepository.save(card);
        return CardIssuedResponse.from(savedCard);

    }

    private Card createCard(Account account, Customer customer) {
        String cardNumber = getCardNumber();
        String cvc = createNumber(3, "");
        boolean isIssued = true; //발급여부
        LocalDateTime expiredAt = LocalDateTime.now().plusYears(3);
        boolean cardStatus = true;

        return Card.builder().customer(customer).account(account).cardName(CARDNAME).cardNumber(cardNumber)
                .cvc(cvc).isIssued(isIssued).expiredAt(expiredAt).cardStatus(cardStatus).build();
    }

    @Transactional
    public CardIssuedResponse cardReissued(CardReissuedRequest cardReissuedRequest) {
        Card card = cardRepository.findByCardNumberAndCardStatusTrueAndExpiredAtGreaterThan(
                        cardReissuedRequest.getCardNumber(), LocalDateTime.now())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CARDNUMBER_NOT_FOUND)
                );

        Customer customer = customerRepository.findByCi(cardReissuedRequest.getCi()).orElseThrow(
                () -> new CustomException(ErrorCode.IDENTITYCODE_NOT_FOUND)
        );

        if (!card.getCustomer().equals(customer)) {
            throw new CustomException(ErrorCode.CARD_OWNER_MISMATCH);
        }

        Account account = accountRepository.findByFintechUseNum(cardReissuedRequest.getFintechUseNum())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.FINTECHCODE_NOT_FOUND)
                );

        if (!card.getAccount().equals(account)) {
            throw new CustomException(ErrorCode.ACCOUNT_MISMATCH_WITH_CARD);
        } else if (!card.getAccount().getBank().getBankCode().equals(BANK_CODE)) {
            throw new CustomException(ErrorCode.CARD_NOT_FROM_WOORI_BANK);
        }

        card.changeCardStatus(false);
        Card createdcard = createCard(account, customer);
        Card savedCard = cardRepository.save(createdcard);
        return CardIssuedResponse.from(savedCard);
    }

    @Retryable
    private String getCardNumber() {
        String cardNumber = createNumber(9, CARD_PREFIX);
        Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);

        if (optionalCard.isEmpty()) {
            return cardNumber;
        }
        throw new CustomException(ErrorCode.CARDNUMBER_DUPLICATED);
    }

    private String createNumber(int count, String prefix) {
        StringBuilder randomNum = new StringBuilder();
        randomNum.append(prefix);
        for (int i = 0; i < count; i++) {
            int createNum = RANDOM.nextInt(10); // 0~9 사이의 랜덤 숫자 생성
            randomNum.append(createNum);
        }
        return randomNum.toString();
    }
}
