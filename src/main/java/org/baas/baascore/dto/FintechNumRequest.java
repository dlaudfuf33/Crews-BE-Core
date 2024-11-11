package org.baas.baascore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class FintechNumRequest {

    @NotBlank
    private String identityCode;

    @NotBlank
    @Length(min = 10,max = 14)
    private String accountNumber;

}
