package org.baas.baascore.service;

import lombok.RequiredArgsConstructor;
import org.baas.baascore.dto.CardIssuedResponse;
import org.baas.baascore.dto.CardReissuedRequest;
import org.baas.baascore.dto.CommonRequest;
import org.baas.baascore.excaption.CardDuplicatedException;
import org.baas.baascore.excaption.CardNumberNotFoundException;
import org.baas.baascore.excaption.FintechNumberNotFoundException;
import org.baas.baascore.excaption.IdentityCodeNotFoundException;
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
        Customer customer = customerRepository.findByIdentityCode(commonRequest.getIdentityCode()).orElseThrow(
                IdentityCodeNotFoundException::new
        );
        Account account = accountRepository.findByFintechUseNum(commonRequest.getFintechUseNum()).orElseThrow(
                FintechNumberNotFoundException::new
        );
        if(!account.getBank().getBankCode().equals(BANK_CODE)) {
            throw new IllegalStateException("우리은행의 계좌가 아닙니다.");
        }
        Card card = createCard(account, customer);
        Card savedCard = cardRepository.save(card);
        return CardIssuedResponse.from(savedCard);

    }

    private Card createCard(Account account, Customer customer){
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
                cardReissuedRequest.getCardNumber(),LocalDateTime.now()).orElseThrow(
                CardNumberNotFoundException::new
        );

        Customer customer = customerRepository.findByIdentityCode(cardReissuedRequest.getIdentityCode()).orElseThrow(
                IdentityCodeNotFoundException::new
        );

        if(!card.getCustomer().equals(customer)) {
            throw new IllegalStateException("카드소유주와 서비스 요청자기 다릅니다.");
        }

        Account account = accountRepository.findByFintechUseNum(cardReissuedRequest.getFintechUseNum()).orElseThrow(
                FintechNumberNotFoundException::new
        );

        if (!card.getAccount().equals(account)) {
            throw new IllegalStateException("카드와 연결되어있는 계좌가 핀테크번호의 계좌와 다릅니다.");
        } else if (!card.getAccount().getBank().getBankCode().equals(BANK_CODE)) {
            throw new IllegalStateException("우리은행의 계좌의 카드가 아닙니다.");
        }

        card.changeCardStatus(false);
        Card createdcard = createCard(account, customer);
        Card savedCard = cardRepository.save(createdcard);
        return CardIssuedResponse.from(savedCard);
    }

    @Retryable
    private String getCardNumber() {
        String cardNumber = createNumber(8, CARD_PREFIX);
        Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);

        if(optionalCard.isEmpty()){
            return cardNumber;
        }
        throw new CardDuplicatedException();
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
