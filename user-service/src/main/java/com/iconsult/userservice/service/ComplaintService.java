package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.ComplaintDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface ComplaintService {
    public CustomResponseEntity createComplaint(ComplaintDto complaintDto);
    public CustomResponseEntity showComplaint(Long CustomerId);
}
