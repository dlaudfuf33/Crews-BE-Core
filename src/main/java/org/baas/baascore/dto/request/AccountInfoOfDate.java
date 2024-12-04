package org.baas.baascore.dto.request;

import org.baas.baascore.util.TranType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfoOfDate {

	@NotBlank
	private String ci;

	@NotBlank
	private String fintechUseNum;

	@NotNull
	private Integer year;

	@NotNull
	private Integer month;

	@NotNull
	private TranType tranType;

}
