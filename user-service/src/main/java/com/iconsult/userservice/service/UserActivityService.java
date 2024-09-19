package com.iconsult.userservice.service;


import com.iconsult.userservice.dto.ActivityRecordResponse;
import com.iconsult.userservice.dto.UserActivityRequest;
import com.iconsult.userservice.model.entity.UserActivity;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.io.Serializable;
import java.util.List;

public interface UserActivityService extends Serializable {
    public CustomResponseEntity saveUserActivity(UserActivityRequest userActivity);
    public CustomResponseEntity<List<ActivityRecordResponse>> recordOfUserActivity(Long customerId, int days);
}
