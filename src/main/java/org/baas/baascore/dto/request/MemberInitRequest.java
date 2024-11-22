package org.baas.baascore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInitRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

}
