package org.baas.baascore.excaption;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    BANK_NOT_FOUND("해당하는 은행을 찾을 수 없습니다."),
    CUSTOMER_NOT_FOUND("해당하는 고객을 찾을 수 없습니다."),
    HASH_ALGORITHM_NOT_FOUND("해시 알고리즘을 찾을 수 없습니다."),
    IDENTITYCODE_NOT_FOUND("해당하는 식별자코드를 찾을 수 없습니다."),
    FINTECHCODE_NOT_FOUND("해당하는 핀테크번호를 찾을 수 없습니다."),
    MEMBER_NOT_EQUALS("계좌주인과 서비스 요청자가 다릅니다."),
    BALANCE_NOT_ZERO("잔액이 0원이 아닙니다. 잔액을 비워주세요."),
    ACCOUNTNUMBER_NOT_FOUND("계좌번호가 맞지 않습니다."),
    ACCOUNTNUMBER_DUPLICATED("계좌번호가 중복되었습니다."),
    INSUFFICIENT_BALANCE("잔액이 부족합니다."),
    TRANSFER_FAILED("이체 거래 처리 중 오류가 발생했습니다."),
    WITHDRAW_ACCOUNT_NOT_FOUND("출금 계좌를 찾을 수 없습니다."),
    DEPOSIT_ACCOUNT_NOT_FOUND("입금 계좌를 찾을 수 없습니다."),
    TRANSACTION_NOT_FOUND("해당 거래내역을 찾을 수 없습니다."),
    NOT_SET_SA_SK("SA와 SK가 설정되어 있지 않습니다.");
    private final String message;
}
