package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.constant.PinStatus;
import com.iconsult.userservice.custome.Regex;
import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.model.entity.Card;
import com.iconsult.userservice.Util.EncryptionUtil;
import com.iconsult.userservice.model.dto.request.CustomerDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.Device;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.repository.SettingRepository;
import com.iconsult.userservice.service.SettingService;
import com.zanbeel.customUtility.exception.ServiceException;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    Regex regex;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    @Override
    public CustomResponseEntity setDevicePin(Long id,SettingDTO settingDTO) {
         try {

             if (id != null && settingDTO.getDevicePin() != null) {

              String pin = settingDTO.getDevicePin();

//              if(!pin.matches("\\d{4}")){
//                  LOGGER.error("Pin must be exactly 4 digits");
//                  return CustomResponseEntity.error("Pin must be exactly 4 digits");
//              }

                 // Check if the pin is sequential (e.g., 1234, 2345, etc.)
                 if (isSequential(pin)) {
                     LOGGER.error("Sequential pins are not allowed");
                     return CustomResponseEntity.error("Sequential pins are not allowed");
                 }

                 String jpql = "SELECT c FROM Device c WHERE c.id = :deviceId";
                 Map<String, Object> params = new HashMap<>();
                 params.put("deviceId", id);

                 Device device = cardGenericDao.findOneWithQuery(jpql, params);

                 if (device.getDevicePin() != null) {
                     LOGGER.error("Pin already exists");
                     return CustomResponseEntity.error("Pin already exists");
                 }
                 else
                 {
                     device.setDevicePin(settingDTO.getDevicePin());
                     device.setPinStatus(PinStatus.ACTIVE);
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
             LOGGER.error("Exception occurred: ", e);
             return new CustomResponseEntity<>(e.getMessage(),"Failed to set pin");
         }
    }

    private boolean isSequential(String pin) {
        // Convert pin to a char array for easy manipulation
        char[] digits = pin.toCharArray();

        // Check for sequential increment (e.g., 1234, 2345, etc.)
        for (int i = 1; i < digits.length; i++) {
            if (digits[i] != digits[i - 1] + 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CustomResponseEntity updateDevicePin(String id, SettingDTO settingDTO) {

        try
        {
            Device existingDevice = settingRepository.findById(Long.valueOf(id)).orElseThrow();
            if(existingDevice.getDevicePin().equals(settingDTO.getOldPin()))
            {
                String pin = settingDTO.getDevicePin();

              if(!pin.matches("\\d{4}")){
                  LOGGER.error("Pin must be exactly 4 digits");
                  return CustomResponseEntity.error("Pin must be exactly 4 digits");
              }

                // Check if the pin is sequential (e.g., 1234, 2345, etc.)
                if (isSequential(pin)) {
                    LOGGER.error("Sequential pins are not allowed");
                    return CustomResponseEntity.error("Sequential pins are not allowed");
                }

                existingDevice.setDevicePin(settingDTO.getDevicePin());
                existingDevice.setPinStatus(PinStatus.ACTIVE);
                settingRepository.save(existingDevice);
                return customResponseEntity = new CustomResponseEntity<>("Pin Updated Successfully!");
            }
            else
            {
                return customResponseEntity = new CustomResponseEntity<>("Old PIN Invalid");
            }
        }
        catch (EntityNotFoundException e) {
            // Handle case where the device is not found
            LOGGER.error("EntityNotFoundException occurred: ", e);
            return new CustomResponseEntity<>(e.getMessage(), "Failed to update pin");
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            LOGGER.error("Exception occurred: ", e);
            return new CustomResponseEntity<>(e.getMessage(),"Failed to update pin");
        }
    }

    @Override
    public CustomResponseEntity setTransactionLimit(String accountNumber, Long customerId, Double transferLimit) {
        CustomResponseEntity customResp = regex.checkAccountNumberFormat(accountNumber);
        if(!customResp.isSuccess()){
            return customResp;
        }
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

    public CustomResponseEntity changePassword(String oldPassword,String newPassword, Long id) throws Exception {
        try{
            String newEncryptedPassword;
            Customer customer = customerRepository.findById(id).orElse(null);
            if(Objects.isNull(customer)){
                LOGGER.error("customer does not exist");
                return CustomResponseEntity.error("customer does not exist");
            }
            String decryptedSavedPassword = EncryptionUtil.decrypt(customer.getPassword(),"t3dxltZbN3xYbI98nBJX3y6ZYZk1M9cukRIhgIz02mA=");
            if(!decryptedSavedPassword.equals(oldPassword)){
                LOGGER.error("password does not exist");
                return CustomResponseEntity.error("password does not match");
            }
//            newEncryptedPassword = EncryptionUtil.decrypt(newPassword ,"t3dxltZbN3xYbI98nBJX3y6ZYZk1M9cukRIhgIz02mA=");
            newEncryptedPassword = EncryptionUtil.encrypt("t3dxltZbN3xYbI98nBJX3y6ZYZk1M9cukRIhgIz02mA=",newPassword);
            customer.setPassword(newEncryptedPassword);
            customerRepository.save(customer);


        }catch (Exception e){
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("change password Exception" + e);

        }
        return CustomResponseEntity.builder().message("Password Change Successfully").success(true).build();
    }

    @Override
    public CustomResponseEntity updateProfile(CustomerDto customerDto) {
        Customer customer = customerRepository.findById(customerDto.getClientNo()).orElse(null);
        if(Objects.isNull(customer)){
            LOGGER.error("customer does not exist");
            return CustomResponseEntity.error("customer does not exist");
        }
        if(!customerDto.getMobileNumber().isEmpty() || !customerDto.getEmail().isEmpty()){
            customer.setMobileNumber(customer.getMobileNumber());
            customer.setEmail(customer.getEmail());
            customerRepository.save(customer);
        }
        return CustomResponseEntity.builder().message("Profile Updated Successfully").success(true).build();
    }
    @Override
    public CustomResponseEntity deactivatePin(Long customerId, String unique) {
        try {
            Device device = settingRepository.findByCustomerIdAndUnique1AndPinStatus(customerId, unique, PinStatus.ACTIVE);

            if (device == null) {
                return CustomResponseEntity.error("Device already De-Activated");
            }

            // Deactivate the PIN
            device.setDevicePin(null);
            device.setPinStatus(PinStatus.INACTIVE);
            settingRepository.save(device);

            // success response
            return new CustomResponseEntity<>(/*"success",*/ "Pin De-Activated Successfully.");

        } catch (DataAccessException e) {
            // Handle database access issues
            return CustomResponseEntity.error("Database error: " + e.getMessage());
        } catch (Exception e) {
            // Handle other unexpected exceptions
            return CustomResponseEntity.error("An unexpected error occurred: " + e.getMessage());
        }
    }
}