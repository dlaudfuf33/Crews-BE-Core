package org.baas.baascore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdentityRequest {

    @NotBlank
    private String ci;
}
