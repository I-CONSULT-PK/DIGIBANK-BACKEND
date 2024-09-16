package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.dto.UserActivityRequest;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.UserActivity;
import com.iconsult.userservice.service.UserActivityService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityImpl implements UserActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
   private GenericDao <UserActivity> genericDao;

    @Autowired
    CustomResponseEntity customResponseEntity;


    public CustomResponseEntity saveUserActivity(String usedId , String activity)
    {

        UserActivity userActivity = new UserActivity();
        userActivity.setActivity(activity);
        LocalDateTime dateTime = LocalDateTime.now();
        userActivity.setActivityDate(LocalDateTime.now());
        genericDao.saveOrUpdate(userActivity);
        customResponseEntity.setData(userActivity);

        genericDao.saveOrUpdate(userActivity);
        LOGGER.info("User Activity Is Updated");
        return customResponseEntity;
    }

    public CustomResponseEntity saveUserActivity(UserActivityRequest userActivityRequest)
    {
        UserActivity userActivity = new UserActivity();
        userActivity.setActivity(userActivityRequest.getUserActivity());
        userActivity.setActivityDate(LocalDateTime.now());
        userActivity.setCustomerId(userActivityRequest.getCustomerId());
        genericDao.saveOrUpdate(userActivity);
        customResponseEntity.setData(userActivity);
        genericDao.saveOrUpdate(userActivity);
        LOGGER.info("User Activity Is Updated");
        return customResponseEntity;
    }


}
