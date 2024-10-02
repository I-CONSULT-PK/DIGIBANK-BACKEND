package DigiBank.BillPaymentService.exception;

import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponseEntity<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        CustomResponseEntity<String> response = new CustomResponseEntity<>(
                1001, // Custom error code
                "Invalid value for utilityType. Please use one of the following values: " +
                        "INTERNET, CREDIT_CARD, WATER, GAS, TELECOMMUNICATION, ELECTRICITY.",
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomResponseEntity<String>> handleHttpMessageNotReadable(MethodArgumentTypeMismatchException ex) {
        CustomResponseEntity<String> response = new CustomResponseEntity<>(
                1001, // Custom error code
                "Invalid value for utilityType. Please use one of the following values: " +
                        "INTERNET, CREDIT_CARD, WATER, GAS, PTCL, ELECTRICITY.",
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
