package org.baas.baascore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.baas.baascore.util.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 고객 정보를 관리하는 Customer 엔티티
 */
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Table(name = "bank_member")
public class Customer extends BaseTimeEntity {
    // 고객 고유 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 고객 이메일, 시스템 내에서 고유해야 함
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_num", nullable = false, unique = true,length = 11)
    private String phoneNum;

    // 고객 이름
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ci",length = 88, nullable = false, unique = true)
    private String ci;

    // 한 고객이 여러 계좌를 소유할 수 있도록 양방향 매핑 추가
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

}
