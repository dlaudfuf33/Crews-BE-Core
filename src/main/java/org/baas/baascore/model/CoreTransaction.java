package org.baas.baascore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baas.baascore.util.BaseTimeEntity;
import org.baas.baascore.util.StatusType;

import java.util.ArrayList;
import java.util.List;

/**
 * 트랜잭션 엔티티 - History 엔티티와 연결된 트랜잭션 정보를 관리
 */
@Getter
@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreTransaction extends BaseTimeEntity {
    // 트랜잭션 고유 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 트랜잭션과 연결된 거래 내역 정보
    @Builder.Default
    @OneToMany(mappedBy = "coreTransaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TransactionHistory> tranHistory = new ArrayList<>();


    // 트랜잭션 상태 (예: 성공, 실패 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusType status;

    public void markSuccess() {
        this.status = StatusType.SUCCESS;
    }

    public void markFail() {
        this.status = StatusType.FAIL;
    }


}
