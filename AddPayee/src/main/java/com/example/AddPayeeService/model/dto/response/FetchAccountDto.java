package com.example.AddPayeeService.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FetchAccountDto {
    String accountNumber;
    String accountTitle;
    String bankName;
}
