package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.Device;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.repository.SettingRepository;
import com.iconsult.userservice.service.SettingService;
import com.zanbeel.customUtility.exception.ServiceException;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingServiceImpl implements SettingService {


    @Autowired
    private GenericDao<Device> cardGenericDao;
    @Autowired
    private GenericDao<Account> accountGenericDao;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    CustomResponseEntity customResponseEntity;

    @Autowired
    AccountRepository accountRepository;

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
             LOGGER.error("Exception occurred: ");
         }
        return null;
    }

    @Override
    public CustomResponseEntity setTransactionLimit(String accountNumber, Long customerId, Double transferLimit) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        String jpql = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber and a.customer= :customer";
        Map<String, Object> params = new HashMap<>();
        params.put("accountNumber", accountNumber);
        params.put("customer", customer);
        Account account = accountGenericDao.findOneWithQuery(jpql, params);
        account.setTransactionLimit(transferLimit);
        accountGenericDao.saveOrUpdate(account);
        CustomResponseEntity customResponse = new CustomResponseEntity<>( "Transaction limit set to : " + transferLimit);
        return customResponse;
    }
}