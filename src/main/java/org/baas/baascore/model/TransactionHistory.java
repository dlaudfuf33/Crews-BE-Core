package org.baas.baascore.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.util.BaseTimeEntity;
import org.baas.baascore.util.TranType;

import java.math.BigDecimal;

/**
 * 거래 내역을 관리하는 History 엔티티
 */
@Getter
@Entity
@Table(name = "core_history")
public class TransactionHistory extends BaseTimeEntity {
    // 거래 기록의 고유 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 거래가 발생한 계좌
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_account_id", nullable = false)
    private Account account;


    // 카드가 연관된 경우 해당 카드 정보 (선택적 필드)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_card", nullable = true)
    private Card card;

    // 거래 유형 (예: 입금, 출금 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "tran_type", nullable = false)
    private TranType tranType;

    // 거래 금액
    @Column(name = "tran_amt", nullable = false)
    private BigDecimal tranAmt;

    // 거래 후 계좌 잔액
    @Column(name = "after_balance_amt", nullable = false)
    private BigDecimal afterBalanceAmt;

    // 출금 상대 이름
    @Column(name = "counterparty_Name", nullable = false)
    private String counterpartyName;

    // 출금 상대 계좌 번호
    @Column(name = "counterparty_account_num", nullable = false)
    private String counterpartyAccountNum;

    // 상대방 은행 코드 (선택적 필드)
    @Column(name = "counterparty_bank_code", nullable = false)
    private String counterpartyBankCode;

    // 거래 설명, 최대 50자 (선택적 필드)
    @Column(name = "description", nullable = true, length = 50)
    private String description;

    // CoreTransaction과의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_transaction_id", nullable = false)
    private CoreTransaction coreTransaction;

    public TransactionHistory() {

    }

    @Builder
    public TransactionHistory(Account account, TranType tranType, BigDecimal tranAmt,BigDecimal afterBalanceAmt, Account countryAccount, CoreTransaction coreTransaction,String description) {
        this.account = account;
        this.tranType = tranType;
        this.tranAmt = tranAmt;
        this.afterBalanceAmt = afterBalanceAmt;
        this.counterpartyName = countryAccount.getCustomer().getName();
        this.counterpartyAccountNum = countryAccount.getAccountNumber();
        this.counterpartyBankCode = countryAccount.getBank().getBankCode();
        this.coreTransaction = coreTransaction;
        this.description = description;
    }


    public void recordAfterTransactionBalance(BigDecimal balance) {
        this.afterBalanceAmt = balance;
    }
}
