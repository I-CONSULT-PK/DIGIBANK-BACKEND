package com.iconsult.userservice.model.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePinDto {

    private Long customerId;

    @NotNull(message = "Card Number Required")
    private String cardNumber;
    @NotNull(message = "Old PIN is required")
    private String oldPin;
    @NotNull(message = "New PIN is required")
    private String newPin;
    @NotNull(message = "Confirmation PIN is required")
    private String confirmNewPin;

}
