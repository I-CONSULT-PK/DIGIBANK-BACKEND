package com.iconsult.userservice.model.dto.response;

import com.iconsult.userservice.model.dto.request.AccountDto;
import com.iconsult.userservice.model.dto.request.BankDto;
import com.iconsult.userservice.model.dto.request.BranchDto;
import com.iconsult.userservice.model.dto.request.GlobalId;
import com.iconsult.userservice.model.entity.Customer;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private String firstName;
    private String lastName;
    private String email;
    private BranchDto cbsBranchDto;
    private BankDto cbsBankDto;
    private AccountDto account;
    @Valid
    private GlobalId globalId;
    @Valid
    private Customer customer;

    private String cnicNumber;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BranchDto getCbsBranchDto() {
        return cbsBranchDto;
    }

    public void setCbsBranchDto(BranchDto cbsBranchDto) {
        this.cbsBranchDto = cbsBranchDto;
    }

    public BankDto getCbsBankDto() {
        return cbsBankDto;
    }

    public void setCbsBankDto(BankDto cbsBankDto) {
        this.cbsBankDto = cbsBankDto;
    }

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    public void setGlobalId(GlobalId globalId) {
        this.globalId = globalId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCnicNumber() {
        return cnicNumber;
    }

    public void setCnicNumber(String cnicNumber) {
        this.cnicNumber = cnicNumber;
    }
}
