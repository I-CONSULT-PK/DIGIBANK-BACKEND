package com.example.AddPayeeService.service;

import com.example.AddPayeeService.model.dto.BanksDto;
import com.example.AddPayeeService.model.dto.CbsAccountDto;
import com.example.AddPayeeService.model.dto.request.AddPayeeRequestDto;
import com.example.AddPayeeService.model.dto.response.AddPayeeResponseDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface AddPayeeService {

    //create


    CustomResponseEntity createBeneficiary(AddPayeeRequestDto addPayeeRequestDto) throws Exception;

    CustomResponseEntity getBeneficiary(AddPayeeRequestDto addPayeeRequestDto) throws Exception;

    //get all added Beneficiaries
    List<CustomResponseEntity<AddPayeeResponseDto>> getAllBeneficiaries(Long customerId) throws Exception;

    //get Single Beneficiary
    CustomResponseEntity getAddPayee(Long BeneId);

    public CustomResponseEntity<List<BanksDto>> getAllBanks();

    CustomResponseEntity deleteBene(Long beneId);

    CustomResponseEntity updateBene(AddPayeeRequestDto addPayeeRequestDto);
    public CustomResponseEntity getAccountDetails(String accountNumber, String bankName);

    CustomResponseEntity getLocalAccountTitle(String senderAccountNumber);
}
