package com.iconsult.userservice.model.dto.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class GlobalId {
    private String globalIdType;

    private long GlobalId;
    private String globalIdStatus;
    private String issueDT;
    private String expiryDT;
    @NotNull(message = "Cnic name cannot be null")
    @Size(min = 13, max = 13, message = "CNIC must be exactly 13 digits")
    private String cnicNumber;

    private String dateOfBirth;

    private String issuePlace;
    private String issueAuthority;
    private String countryIssue;
    private String stateIssue;

}
