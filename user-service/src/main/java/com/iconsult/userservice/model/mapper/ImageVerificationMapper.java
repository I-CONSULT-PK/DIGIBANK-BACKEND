package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.request.ImageVerficationRequest;
import com.iconsult.userservice.model.dto.response.ImageVerificationResponse;
import com.iconsult.userservice.model.entity.ImageVerification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageVerificationMapper {

    ImageVerification dtoToJpe(ImageVerficationRequest verficationRequest);

    ImageVerificationResponse JpeToDto(ImageVerification imageVerification);
}
