package com.admin_service.service;

import com.admin_service.dto.request.CreateModuleDto;
import com.admin_service.dto.request.UtDocSubtypeDto;
import com.admin_service.dto.request.UtDocTypeDto;
import com.admin_service.model.CustomResponseEntity;
import org.springframework.stereotype.Service;

public interface ModuleService {
    CustomResponseEntity createModule(CreateModuleDto createModuleDto);

    CustomResponseEntity getAllModule();

    CustomResponseEntity createUtDocType(UtDocTypeDto utDocTypeDto , Long id);

    CustomResponseEntity getAllUtDocType();

    CustomResponseEntity createSubUtDocType(UtDocSubtypeDto utDocSubtypeDto, Long hdrModuleId, Long utDocTypeId);

    CustomResponseEntity getAllUtSubDocType();

    CustomResponseEntity getModuleById(Long id);


    CustomResponseEntity getUtDocTypeById(Long id);

    CustomResponseEntity getUtDocSubTypeById(Long id);
}
