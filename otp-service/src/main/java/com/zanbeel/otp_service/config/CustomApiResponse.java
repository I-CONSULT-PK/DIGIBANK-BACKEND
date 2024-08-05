package com.zanbeel.otp_service.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomApiResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int errorCode;
    private boolean success;
    private String message;
    private T data;

    private Error error;

    public CustomApiResponse(Error error){
        this.error=error;
    }

    public CustomApiResponse(T data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    public CustomApiResponse(String message) {
        this.success = true;
        this.message = message;
    }

    public CustomApiResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.success = false;
        this.message = message;
    }

    public CustomApiResponse(int errorCode, String message, T data) {
        this.errorCode = errorCode;
        this.success = false;
        this.message = message;
        this.data = data;
    }

    public static <T> CustomApiResponse<T> error(String error) {
        CustomApiResponse<T> response = new CustomApiResponse<>();
        response.setMessage(error);
        response.setErrorCode(1000);
        response.setSuccess(false);
        return response;
    }


}
