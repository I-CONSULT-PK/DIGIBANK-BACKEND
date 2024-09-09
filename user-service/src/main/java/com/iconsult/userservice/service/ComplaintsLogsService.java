package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.ComplainLogsDto;
import com.iconsult.userservice.model.entity.ComplaintsLogs;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface ComplaintsLogsService {
    CustomResponseEntity fetchAllComplaintServices();
    CustomResponseEntity addComplaint(ComplainLogsDto complaintsLogs);
    CustomResponseEntity fetchSpecificComplaint(String service);
}
