package com.iconsult.userservice.model.dto.response;

import com.iconsult.userservice.model.entity.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class DashBoardResponseDto implements Serializable {
    private Double totalBalance;
    private Double lastCredit;
    private Double lastDebit;
    private List<Account> accountList;

    @Override
    public String toString() {
        return "DashBoardResponseDto{" +
                "totalBalance=" + totalBalance +
                ", lastCredit=" + lastCredit +
                ", lastDebit=" + lastDebit +
                ", accountList=" + accountList +
                '}';
    }
}
