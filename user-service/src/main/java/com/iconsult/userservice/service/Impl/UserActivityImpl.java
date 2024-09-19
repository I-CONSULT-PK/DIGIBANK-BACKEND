package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.dto.ActivityRecordResponse;
import com.iconsult.userservice.dto.UserActivityRequest;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.UserActivity;
import com.iconsult.userservice.service.UserActivityService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserActivityImpl implements UserActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
   private GenericDao <UserActivity> genericDao;

    @Autowired
    CustomResponseEntity customResponseEntity;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm:ss");


    public CustomResponseEntity saveUserActivity(String usedId , String activity, String pkr)
    {

        UserActivity userActivity = new UserActivity();
        LocalDateTime dateTime = LocalDateTime.now();
        userActivity.setActivity(activity+" on "+dateTime.format(formatter)+"  "+dateTime);
        userActivity.setPkr(pkr != null ? Double.parseDouble(pkr) : 0.0);
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
        LocalDateTime dateTime = LocalDateTime.now();
        //DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        //LocalDateTime localDateTime = LocalDateTime.parse("2024-08-01 14:13:27.692",dt);
        //userActivity.setActivityDate(localDateTime);
        userActivity.setActivityDate(dateTime);
        userActivity.setCustomerId(userActivityRequest.getCustomerId());
        //userActivity.setActivity(userActivity.getActivity() +" on "+localDateTime.format(formatter));
        userActivity.setActivity(userActivity.getActivity() +" on "+dateTime.format(formatter));
        userActivity.setPkr(userActivityRequest.getPkr() != null ? userActivityRequest.getPkr() : 0.0);
        genericDao.saveOrUpdate(userActivity);
        customResponseEntity.setData(userActivity);
        genericDao.saveOrUpdate(userActivity);
        LOGGER.info("User Activity Is Updated");
        return customResponseEntity;
    }

    @Override
    public CustomResponseEntity<List<ActivityRecordResponse>> recordOfUserActivity(Long customerId, int days) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime dateThreshold = currentDateTime.minus(days, ChronoUnit.DAYS);
        //LocalDateTime dateThreshold = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
        String jpql = "SELECT u FROM UserActivity  u WHERE u.customerId.id = :customerId and " +
                "u.activityDate >= :dateThreshold";
        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("dateThreshold",dateThreshold);
        List<UserActivity> activitiesRecord = genericDao.findWithQuery(jpql,params);
        if(activitiesRecord.isEmpty()){
            LOGGER.info("invalid customer id : "+customerId);
            return CustomResponseEntity.error("No User-Activity found against customer id : "+ customerId);
        }
        LOGGER.info("record found successfully :  "+activitiesRecord);
        List<ActivityRecordResponse> activityRecordResponses = activitiesRecord.stream()
                .sorted(Comparator.comparing(UserActivity::getActivityDate)) // Sort by activityDate
                .map(activity-> {
            ActivityRecordResponse activityRecord = new ActivityRecordResponse();
            activityRecord.setUserActivity(activity.getActivity());
            activityRecord.setPkr(activity.getPkr());
            String formattedDate = activity.getActivityDate().format(formatter); // Assuming activityDate is LocalDateTime
            activityRecord.setActivityDate(formattedDate);
            return activityRecord;
        }).collect(Collectors.toList());
        return new CustomResponseEntity<>(activityRecordResponses,"Success");
    }
}
