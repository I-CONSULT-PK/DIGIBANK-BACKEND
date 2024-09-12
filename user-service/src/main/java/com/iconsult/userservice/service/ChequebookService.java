package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.response.ChequebookDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface ChequebookService {


//    CustomResponseEntity<ChequebookDto> createChequebookRequest(ChequebookDto chequebookDto);
    CustomResponseEntity<ChequebookDto> createChequebookRequest(ChequebookDto chequebookDto, String accountNumber);
    CustomResponseEntity<ChequebookDto> getChequebookById(Long id);
    CustomResponseEntity<List<ChequebookDto>> getAllChequebooks();
    CustomResponseEntity<ChequebookDto> updateChequebookRequest(ChequebookDto chequebookDto, String accountNumber);
    CustomResponseEntity<ChequebookDto> deleteChequebook(Long id);
    CustomResponseEntity<String> cancelChequebookRequest(String accountNumber);

}
