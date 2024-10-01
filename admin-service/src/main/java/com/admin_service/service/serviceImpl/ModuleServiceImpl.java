package com.admin_service.service.serviceImpl;

import com.admin_service.dto.request.CreateModuleDto;
import com.admin_service.dto.request.UtDocSubtypeDto;
import com.admin_service.dto.request.UtDocTypeDto;
import com.admin_service.entity.HdrAdModule;
import com.admin_service.entity.UtDocSubtypeSetup;
import com.admin_service.entity.UtDocTypeSetup;
import com.admin_service.mapper.ModuleMapper;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.repository.ModuleRepository;
import com.admin_service.repository.UtDocTypeSetupRepository;
import com.admin_service.repository.UtSubDocTypeSetupRepository;
import com.admin_service.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    UtDocTypeSetupRepository utDocTypeSetupRepository;

    @Autowired
    UtSubDocTypeSetupRepository utSubDocTypeSetupRepository;

    @Autowired
    ModuleMapper moduleMapper;
    @Override
    public CustomResponseEntity createModule(CreateModuleDto createModuleDto) {
        HdrAdModule hdrAdModule = new HdrAdModule();
        hdrAdModule.setModuleCode(createModuleDto.getModuleCode());
        hdrAdModule.setModuleLtxt(createModuleDto.getModuleLtxt());
        hdrAdModule.setClientId(createModuleDto.getClientId());
        hdrAdModule.setModulesTxt(createModuleDto.getModulesTxt());
        hdrAdModule.setModuleTxt(createModuleDto.getModuleTxt());
        hdrAdModule.setSysDateTime(new Date());
        HdrAdModule savedModule = moduleRepository.save(hdrAdModule);
        return new CustomResponseEntity(savedModule,"Module Saved succesfully");

    }

    @Override
    public CustomResponseEntity getAllModule() {
        List<HdrAdModule> findAll = moduleRepository.findAll();
        return new CustomResponseEntity(findAll, "Data Retrieved Successfully");
    }

    @Override
    public CustomResponseEntity createUtDocType(UtDocTypeDto utDocTypeDto ,Long id) {
        Optional<HdrAdModule> hdrAdModule = moduleRepository.findById(id);
        UtDocTypeSetup utDocTypeSetup = new UtDocTypeSetup();
        utDocTypeSetup.setDocTypeClientId(utDocTypeDto.getDocTypeClientId());
        utDocTypeSetup.setDoctypestxt(utDocTypeDto.getDoctypestxt());
        utDocTypeSetup.setDoctypecode(utDocTypeDto.getDoctypecode());
        utDocTypeSetup.setModulelid(hdrAdModule.get());
        utDocTypeSetup.setSysdatetime(new Date());
        UtDocTypeSetup savedUtDocTypeSetup = utDocTypeSetupRepository.save(utDocTypeSetup);
        return new CustomResponseEntity(savedUtDocTypeSetup, "UtDocType created");
    }

    @Override
    public CustomResponseEntity getAllUtDocType() {
        List<UtDocTypeSetup> allUtDocType = utDocTypeSetupRepository.findAll();
        return new CustomResponseEntity(allUtDocType,"Successfully fetched UT doc type");
    }

    @Override
    public CustomResponseEntity createSubUtDocType(UtDocSubtypeDto utDocSubtypeDto, Long hdrModuleId, Long utDocTypeId) {
        Optional<HdrAdModule> hdrAdModule = moduleRepository.findById(hdrModuleId);
        Optional<UtDocTypeSetup> utDocType = utDocTypeSetupRepository.findById(utDocTypeId);
        UtDocSubtypeSetup utDocSubtypeSetup = new UtDocSubtypeSetup();
        utDocSubtypeSetup.setModulelid(hdrAdModule.get());
        utDocSubtypeSetup.setDoctypeid(utDocType.get());
        utDocSubtypeSetup.setSysdatetime(new Date());
        utDocSubtypeSetup.setDocsubtypestxt(utDocSubtypeDto.getDocsubtypestxt());
        utDocSubtypeSetup.setDocsubtypecode(utDocSubtypeDto.getDocsubtypecode());
        utDocSubtypeSetup.setClientId(utDocSubtypeDto.getClientId());
        UtDocSubtypeSetup savedUtSubDocType = utSubDocTypeSetupRepository.save(utDocSubtypeSetup);
        return new CustomResponseEntity(savedUtSubDocType, "UtSubDocType Created Successfully");
    }


    @Override
    public CustomResponseEntity getAllUtSubDocType() {
        List<UtDocSubtypeSetup> allUtDocType = utSubDocTypeSetupRepository.findAll();
        return new CustomResponseEntity(allUtDocType,"Successfully fetched UT doc type");
    }

    @Override
    public CustomResponseEntity getModuleById(Long id) {
        Optional<HdrAdModule> module = moduleRepository.findById(id);
        return new CustomResponseEntity(module,"Successfully Fetch Module against id "+ id);
    }

    @Override
    public CustomResponseEntity getUtDocTypeById(Long id) {
        Optional<UtDocTypeSetup> docType = utDocTypeSetupRepository.findById(id);
        return new CustomResponseEntity(docType, "Succesfully fetched doctype against id " + id);
    }

    @Override
    public CustomResponseEntity getUtDocSubTypeById(Long id) {
        Optional<UtDocSubtypeSetup> subDocType = utSubDocTypeSetupRepository.findById(id);
        return new CustomResponseEntity(subDocType,"Succesfully fetch doctype against id " + id);
    }


}
