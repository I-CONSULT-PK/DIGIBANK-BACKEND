package com.zanbeel.otp_service.exception;

import com.zanbeel.otp_service.config.CustomApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(OtpException.class)
    public ResponseEntity<CustomApiResponse> otpException(OtpException ex) {
        CustomApiResponse<Object> customApiResponse = new CustomApiResponse<>(String.valueOf(ex.getError()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customApiResponse);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        List<ValidationErrorResponse.Violation> violations = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> new ValidationErrorResponse.Violation(error.getField(), error.getDefaultMessage()))
//                .collect(Collectors.toList());
//
//        ValidationErrorResponse response = new ValidationErrorResponse(violations);
//
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CustomApiResponse handleValidationErrors(MethodArgumentNotValidException ex)
    {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

        return new CustomApiResponse(404, "Field Error", getErrorsMap(errors));
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

}
