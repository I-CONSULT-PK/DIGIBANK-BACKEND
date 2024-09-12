package com.iconsult.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChequeDto {

    private Long id;
    private LocalDate issueDate;
    private String status;
    private String chequeNumber;
//    private Long chequebookId;
}