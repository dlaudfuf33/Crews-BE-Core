package org.baas.baascore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class FintechNumRequest {

    @NotBlank
    private String ci;

    @NotBlank
    @Length(min = 10,max = 14)
    private String accountNumber;

}
