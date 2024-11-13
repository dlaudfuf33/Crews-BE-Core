package org.baas.baascore.excaption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    BANK_NOT_FOUND("해당하는 은행을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CUSTOMER_NOT_FOUND("해당하는 고객을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    HASH_ALGORITHM_NOT_FOUND("해시 알고리즘을 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IDENTITYCODE_NOT_FOUND("해당하는 식별자코드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FINTECHCODE_NOT_FOUND("해당하는 핀테크번호를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MEMBER_NOT_EQUALS("계좌주인과 서비스 요청자가 다릅니다.", HttpStatus.FORBIDDEN),
    BALANCE_NOT_ZERO("잔액이 0원이 아닙니다. 잔액을 비워주세요.", HttpStatus.BAD_REQUEST),
    ACCOUNTNUMBER_NOT_FOUND("계좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WRONG_ACCOUNTNUMBER("잘못된 계좌 번호 입니다.", HttpStatus.BAD_REQUEST),
    ACCOUNTNUMBER_DUPLICATED("계좌번호가 중복되었습니다.", HttpStatus.CONFLICT),
    CARDNUMBER_NOT_FOUND("카드번호가 맞지 않습니다.", HttpStatus.NOT_FOUND),
    CARDNUMBER_DUPLICATED("카드번호가 중복되었습니다.", HttpStatus.CONFLICT),
    INSUFFICIENT_BALANCE("잔액이 부족합니다.", HttpStatus.BAD_REQUEST),
    TRANSFER_FAILED("이체 거래 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WITHDRAW_ACCOUNT_NOT_FOUND("출금 계좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DEPOSIT_ACCOUNT_NOT_FOUND("입금 계좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_CANCELED("이미 취소된 구독입니다.", HttpStatus.BAD_REQUEST),
    UNSUBSCRIBE_FAILED("구독 취소중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SUBSCRIPTION_NOT_FOUND("해당 구독을 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    TRANSACTION_NOT_FOUND("해당 거래내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WRONG_TRANSACTION_TYPE("잘못된 \"TransactionType\"을 입력하셨습니다.", HttpStatus.BAD_REQUEST);
    private final String message;
    private final HttpStatus httpStatus;
}
