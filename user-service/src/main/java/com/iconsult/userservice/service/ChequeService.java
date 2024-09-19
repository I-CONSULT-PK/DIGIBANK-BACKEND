package com.iconsult.userservice.service;


import com.iconsult.userservice.model.dto.response.ChequeDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface ChequeService {

    CustomResponseEntity<ChequeDto> createCheque(ChequeDto chequeDto);

    CustomResponseEntity<ChequeDto> getCheque(String chequeNumber);

    CustomResponseEntity<List<ChequeDto>> getAllCheques();

    CustomResponseEntity<ChequeDto> updateCheque(String chequeNumber, ChequeDto chequeDto);

    CustomResponseEntity<ChequeDto> deleteCheque(String chequeNumber);

    CustomResponseEntity<ChequeDto> cancelCheque(ChequeDto chequeDto, String chequeNumber);

}