package org.baas.baascore.dto.response;
import lombok.Builder;
import lombok.Getter;
import org.baas.baascore.exception.ErrorResponse;

@Getter
@Builder
public class ApiResponse<T> {
    private T data;
    private ErrorResponse error;
    private boolean success;
}
