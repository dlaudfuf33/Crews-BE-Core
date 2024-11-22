package org.baas.baascore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.baas.baascore.model.Product;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String bankCode;
    private String bankName;
    private String productName;
    private double highestRate;
    private double lowestRate;

    public static ProductResponse from(Product product){
        return ProductResponse.builder().id(product.getId())
                .bankCode(product.getBank().getBankCode())
                .bankName(product.getBank().getBankName())
                .productName(product.getProductName())
                .highestRate(product.getHighestRate())
                .lowestRate(product.getLowestRate())
                .build();
    }
}
