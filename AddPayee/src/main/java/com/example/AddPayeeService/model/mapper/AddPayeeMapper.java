package com.example.AddPayeeService.model.mapper;

import com.example.AddPayeeService.model.dto.request.AddPayeeRequestDto;
import com.example.AddPayeeService.model.dto.response.AddPayeeResponseDto;
import com.example.AddPayeeService.model.entity.AddPayee;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddPayeeMapper {
    AddPayee dtoToJpe(AddPayeeRequestDto addPayeeRequestDto);
    AddPayeeResponseDto jpeToDto(AddPayee addPayee);
    List<AddPayeeResponseDto> jpeToDtoList(List<AddPayee> addPayees);
}
