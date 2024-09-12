package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.dto.response.ChequeDto;
import com.iconsult.userservice.service.ChequeService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChequeServiceImpl implements ChequeService {
    @Override
    public CustomResponseEntity<ChequeDto> createCheque(ChequeDto chequeDto) {
        return null;
    }

    @Override
    public CustomResponseEntity<ChequeDto> getChequeById(Long id) {
        return null;
    }

    @Override
    public CustomResponseEntity<List<ChequeDto>> getAllCheques() {
        return null;
    }

    @Override
    public CustomResponseEntity<ChequeDto> updateCheque(Long id, ChequeDto chequeDto) {
        return null;
    }

    @Override
    public CustomResponseEntity<ChequeDto> deleteCheque(Long id) {
        return null;
    }
}
