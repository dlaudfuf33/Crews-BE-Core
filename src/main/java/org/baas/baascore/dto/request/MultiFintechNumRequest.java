package org.baas.baascore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultiFintechNumRequest {

    @NotBlank(message = "CI는 필수입니다.")
    private String ci;

    @NotEmpty(message = "계좌 번호 목록은 필수입니다.")
    @Size(min = 1, message = "적어도 하나의 계좌 번호가 필요합니다.")
    private List<@NotBlank(message = "계좌 번호는 필수입니다.")
        @Size(min = 10, max = 14, message = "계좌 번호는 10자 이상 14자 이하여야 합니다.")
                String> accountNumbers;

}
