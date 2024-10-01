package com.admin_service.controller;

import com.admin_service.dto.request.CreateModuleDto;
import com.admin_service.dto.request.UtDocSubtypeDto;
import com.admin_service.dto.request.UtDocTypeDto;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.ModuleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @PostMapping("/createModule")
    private CustomResponseEntity createModule(@RequestBody CreateModuleDto createModuleDto){
         return moduleService.createModule(createModuleDto);
    }

    @GetMapping("/getAllModule")
    private CustomResponseEntity getAllModule(){
        return moduleService.getAllModule();
    }

    @GetMapping("/getModuleById")
    private CustomResponseEntity getAllModule(@RequestParam Long id){
        return moduleService.getModuleById(id);
    }



    @PostMapping("/createUtDocType/{id}")
    private CustomResponseEntity createDocTypeSetup(@RequestBody UtDocTypeDto utDocTypeDto , @PathVariable Long id){
        return moduleService.createUtDocType(utDocTypeDto,id);
    }


    @GetMapping("/getAllUtDocType")
    private CustomResponseEntity getAllUtDocType(){
        return moduleService.getAllUtDocType();
    }


    @GetMapping("/getUtDocTypeById")
    private CustomResponseEntity getUtDocTypeById(@RequestParam Long id){
        return moduleService.getUtDocTypeById(id);
    }

    @PostMapping("/createSubUtDocType/{hdrModuleId}/subDocType/{utDocTypeId}")
    private CustomResponseEntity createSubDocTypeSetup(@RequestBody UtDocSubtypeDto utDocSubtypeDto ,
                                                       @PathVariable Long hdrModuleId,
                                                       @PathVariable Long utDocTypeId){
        return moduleService.createSubUtDocType(utDocSubtypeDto,hdrModuleId,utDocTypeId);
    }

    @GetMapping("/getAllUtDocSubType")
    private CustomResponseEntity getAllUtDocSubType(){
        return moduleService.getAllUtSubDocType();
    }

    @GetMapping("/getUtDocSubTypeById")
    private CustomResponseEntity getUtDocSubTypeById(@RequestParam Long id){
        return moduleService.getUtDocSubTypeById(id);
    }



}
