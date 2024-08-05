package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.exception.ServiceException;
import com.iconsult.userservice.model.CustomResponseEntity;
import com.iconsult.userservice.model.dto.request.ImageVerficationRequest;
import com.iconsult.userservice.model.dto.response.ImageVerificationResponse;
import com.iconsult.userservice.model.entity.ImageVerification;
import com.iconsult.userservice.model.mapper.ImageVerificationMapper;
import com.iconsult.userservice.repository.ImageVerificationRepository;
import com.iconsult.userservice.service.ImageVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ImageVerifcationServiceImpl implements ImageVerificationService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ImageVerifcationServiceImpl.class);

    @Autowired
    ImageVerificationRepository imageVerificationRepository;

    @Autowired
    ImageVerificationMapper imageVerificationMapper;



    @Override
    public CustomResponseEntity createVerificationImage(ImageVerficationRequest imageVerficationRequest) {
        
        ImageVerification imageVerification = imageVerificationMapper.dtoToJpe(imageVerficationRequest);
        imageVerificationRepository.save(imageVerification);
        CustomResponseEntity customResponseEntity = new CustomResponseEntity();
        customResponseEntity.setData(imageVerification);
        return customResponseEntity;
    }

    @Override
    public CustomResponseEntity updateVerificationImage(ImageVerficationRequest imageVerficationRequest) {
        ImageVerification imageVerification = imageVerificationRepository.findById(imageVerficationRequest.getImageId()).orElse(null);
        if(Objects.isNull(imageVerification)){
            LOGGER.error("Image Does Not Exist With Id" + "" + imageVerficationRequest.getImageId());
            throw new ServiceException(String.format("Image Does Not Exist With Id" + "" + imageVerficationRequest.getImageId()));
        }
        if(Objects.isNull(imageVerficationRequest.getName())){
            LOGGER.error("Image Name Is Null" + "" + imageVerficationRequest.getName());
            throw new ServiceException(String.format("mage Name Is Null" + "" + imageVerficationRequest.getName()));
        }
        if(Objects.isNull(imageVerficationRequest.getSrc())){
            LOGGER.error("Image Src Is Null" + "" + imageVerficationRequest.getSrc());
            throw new ServiceException(String.format("image Src Is Null" + "" + imageVerficationRequest.getSrc()));
        }
        if(Objects.isNull(imageVerficationRequest.getType())){
            LOGGER.error("Image Type Is Null" + "" + imageVerficationRequest.getType());
            throw new ServiceException(String.format("mage Type Is Null" + "" + imageVerficationRequest.getType()));
        }

        imageVerification.setName(imageVerficationRequest.getName());
        imageVerification.setSrc(imageVerficationRequest.getSrc());
        imageVerification.setType(imageVerficationRequest.getType());
        imageVerificationRepository.save(imageVerification);
        CustomResponseEntity customResponseEntity = new CustomResponseEntity();
        customResponseEntity.setData(imageVerification);
        return customResponseEntity;
    }

    @Override
    public ImageVerificationResponse getVerficationImageById(Long Id) {

        ImageVerification imageVerification = imageVerificationRepository.findById(Id).orElse(null);
        if(Objects.isNull(imageVerification)){

            LOGGER.error("Image Does Not Exist With Id" + "" + Id);
            throw new ServiceException(String.format("Image Does Not Exist With Id" + "" + Id));
        }
        return imageVerificationMapper.JpeToDto(imageVerification);

    }

    @Override
    public String deleteVerficationImageById(Long Id) {

        ImageVerification imageVerification = imageVerificationRepository.findById(Id).orElse(null);
        if(Objects.isNull(imageVerification)){

            LOGGER.error("Image Does Not Exist With Id" + "" + Id);
            throw new ServiceException(String.format("Image Does Not Exist With Id" + "" + Id));
        }
        imageVerificationRepository.delete(imageVerification);

        return "Verification Image Deleted Successfully";

    }
}
