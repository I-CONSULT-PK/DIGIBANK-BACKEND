package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.CustomResponseEntity;
import com.iconsult.userservice.model.dto.request.ImageVerficationRequest;
import com.iconsult.userservice.model.dto.response.ImageVerificationResponse;
import com.iconsult.userservice.service.ImageVerificationService;
import com.iconsult.userservice.service.Impl.ImageVerifcationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/customer")
public class ImageVerificationController {

    @Autowired
    ImageVerifcationServiceImpl imageVerifcationService;

    @GetMapping("/getImage/{id}")
   public ImageVerificationResponse getVerifiedImage(@PathVariable Long id){
        return imageVerifcationService.getVerficationImageById(id);
    }

    @PostMapping("/createImage")
    public CustomResponseEntity createImageVerification(@Valid @RequestBody ImageVerficationRequest imageVerficationRequest){
        return imageVerifcationService.createVerificationImage(imageVerficationRequest);
    }


    @PostMapping("/updateImage")
    public CustomResponseEntity updateImageVerification(@Valid @RequestBody ImageVerficationRequest imageVerficationRequest){
        return imageVerifcationService.updateVerificationImage(imageVerficationRequest);
    }

    @DeleteMapping("/deleteVerficationImage/{id}")
    public String deleteImageVerficationImage(@PathVariable Long id){
        return imageVerifcationService.deleteVerficationImageById(id);
    }






}
