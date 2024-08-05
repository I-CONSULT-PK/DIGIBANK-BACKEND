package com.iconsult.userservice.service;

import com.iconsult.userservice.model.CustomResponseEntity;
import com.iconsult.userservice.model.dto.request.ImageVerficationRequest;
import com.iconsult.userservice.model.dto.response.ImageVerificationResponse;

public interface ImageVerificationService {

    public CustomResponseEntity createVerificationImage(ImageVerficationRequest imageVerficationRequest);

    public CustomResponseEntity updateVerificationImage(ImageVerficationRequest imageVerficationRequest);

    ImageVerificationResponse getVerficationImageById(Long Id);

    String deleteVerficationImageById(Long Id);




}
