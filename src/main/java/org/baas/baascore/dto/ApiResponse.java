package org.baas.baascore.dto;
import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.excaption.ErrorResponse;

@Getter
@Builder
public class ApiResponse<T> {
    private T data;
    private ErrorResponse error;
    private boolean success;
}
