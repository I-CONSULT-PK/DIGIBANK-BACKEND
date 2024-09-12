package com.iconsult.userservice.service;


import com.iconsult.userservice.model.dto.response.ChequeDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface ChequeService {

    CustomResponseEntity<ChequeDto> createCheque(ChequeDto chequeDto);
    CustomResponseEntity<ChequeDto> getChequeById(Long id);
    CustomResponseEntity<List<ChequeDto>> getAllCheques();
    CustomResponseEntity<ChequeDto> updateCheque(Long id, ChequeDto chequeDto);
    CustomResponseEntity<ChequeDto> deleteCheque(Long id);



}