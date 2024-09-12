package com.iconsult.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChequebookDto {

    private Long id;
    private String checkType;
    private String status;
    private LocalDate requestDate;
    private int chequePages;

    private String branchCode;
    private String accountNumber;

    private List<ChequeDto> cheques;
//    private Customer customer;

//    Long customerId;
}