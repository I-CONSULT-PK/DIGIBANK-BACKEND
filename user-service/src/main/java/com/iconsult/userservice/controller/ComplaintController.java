package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.request.ComplainLogsDto;
import com.iconsult.userservice.model.dto.request.ComplaintDto;
import com.iconsult.userservice.model.entity.ComplaintsLogs;
import com.iconsult.userservice.service.ComplaintService;
import com.iconsult.userservice.service.ComplaintsLogsService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/complaint")
public class ComplaintController {
    Logger logger = LoggerFactory.getLogger(ComplaintController.class);
    @Autowired
    ComplaintService complaintService;
    @Autowired
    ComplaintsLogsService complaintsLogsService;
    @PostMapping("/create")
    public CustomResponseEntity createCompliant(@Valid @RequestBody ComplaintDto complaintDto){
        return complaintService.createComplaint(complaintDto);
    }
    @GetMapping("/fetch")
    public CustomResponseEntity fetchComplaintSpecificCustomer(@Valid @RequestParam("customerId")Long customerId){
        return complaintService.showComplaint(customerId);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addComplaintType(@Valid @RequestBody ComplainLogsDto complaintsLogs){
        logger.info(complaintsLogs.getComplaintType());
        return new ResponseEntity<>(complaintsLogsService.addComplaint(complaintsLogs),HttpStatus.ACCEPTED);
    }
    @RequestMapping("/list")
    public CustomResponseEntity fetchAllComplaintType(){
        return complaintsLogsService.fetchAllComplaintServices();
    }
}
