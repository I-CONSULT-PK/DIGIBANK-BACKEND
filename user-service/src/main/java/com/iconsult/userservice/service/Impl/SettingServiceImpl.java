package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.entity.Card;
import com.iconsult.userservice.model.entity.Device;
import com.iconsult.userservice.repository.SettingRepository;
import com.iconsult.userservice.service.SettingService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingServiceImpl implements SettingService {


    @Autowired
    private GenericDao<Device> cardGenericDao;

    @Autowired
    CustomResponseEntity customResponseEntity;

    @Autowired
    private SettingRepository settingRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    @Override
    public CustomResponseEntity setDevicePin(String deviceName, String devicePin) {


         try {

             if (deviceName != null && devicePin != null)
             {
                 String jpql = "SELECT c FROM Device c WHERE c.deviceName = :deviceName";
                 Map<String, Object> params = new HashMap<>();
                 params.put("deviceName", deviceName);

                 Device device = cardGenericDao.findOneWithQuery(jpql, params);


                 if (device.getDevicePin() != null) {
                     LOGGER.error("Pin already exists");
                     return CustomResponseEntity.error("Pin already exists");

                 }
                 else
                 {
                     device.setDevicePin(devicePin);
                     cardGenericDao.saveOrUpdate(device);
                     return customResponseEntity = new CustomResponseEntity<>("Pin set Successfully!");
                 }
             }
             else
            {
                LOGGER.error("deviceName & devicePin are empty");
                return CustomResponseEntity.error("deviceName & devicePin are empty");
            }
         }
         catch (Exception e) {
//              Handle exceptions
             LOGGER.error("Exception occurred: ", e);
         }
        return null;
    }
}