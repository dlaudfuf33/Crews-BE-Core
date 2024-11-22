package org.baas.baascore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baas.baascore.util.BaseTimeEntity;

/***
 * 은행의 상품(모임,개인 통장) 엔티티
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bank_product")
public class Product extends BaseTimeEntity {
    // 상품 고유 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 주인 은행 (은행:상품 1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    private Bank bank;

    // 상품 이름
    @Column(name= "name",nullable = false)
    private String productName;

    // 상품 최고이자율
    @Column(name= "highest_rate",nullable = false)
    private double highestRate;

    // 상품 최저이자율
    @Column(name= "lowest_rate",nullable = false)
    private double lowestRate;
}
