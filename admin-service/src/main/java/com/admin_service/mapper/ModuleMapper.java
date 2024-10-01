package com.admin_service.mapper;

import com.admin_service.dto.request.CreateModuleDto;
import com.admin_service.entity.HdrAdModule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    CreateModuleDto toDTO(HdrAdModule hdrAdModule);

    HdrAdModule toEntity(CreateModuleDto createModuleDto);
}
