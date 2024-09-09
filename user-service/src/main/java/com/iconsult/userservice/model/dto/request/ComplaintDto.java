package com.iconsult.userservice.model.dto.request;

import com.iconsult.userservice.model.entity.Customer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintDto {
    @NotNull(message = "Customer Id Required")
    private long customerId;
    @NotNull(message = "Complaint Type Required")
    @Size(min = 02, message = "you enter complaint type empty")
    private String services;
    @NotNull
    @Size(min = 10, max = 250,message = "Minimum 10 Character Required")
    private String summary;
}
