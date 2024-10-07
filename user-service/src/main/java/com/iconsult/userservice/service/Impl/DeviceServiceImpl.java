package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.Util.Util;
import com.iconsult.userservice.model.dto.request.DeviceDetailDto;
import com.iconsult.userservice.model.dto.request.DeviceDto;
import com.iconsult.userservice.model.dto.request.SettingDTO;
import com.iconsult.userservice.model.dto.request.SignUpDto;
import com.iconsult.userservice.model.entity.*;
import com.iconsult.userservice.model.mapper.DeviceMapper;
import com.iconsult.userservice.exception.ServiceException;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.repository.*;
import com.iconsult.userservice.service.DeviceService;
import com.iconsult.userservice.service.EmailService;
import com.iconsult.userservice.service.JwtService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.sql.ast.SqlTreeCreationLogger.LOGGER;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private Customer customer;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomResponseEntity customResponseEntity;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AppConfigurationImpl appConfigurationImpl;

    @Autowired
    private GenericDao<Device> cardGenericDao;

    private CustomResponseEntity response;

    @Autowired
    ImageVerificationRepository imageVerificationRepository;

    @Autowired
    private SettingRepository settingRepository;
//    @Autowired
//    DeviceRepository deviceRepository;

    public CustomResponseEntity registerDevice(SignUpResponse customer) {
        LOGGER.info("Device registration request received");

        // Check if customer account exists
        SignUpResponse existingCustomer = customerService.accountExist(customer.getGlobalId().getCnicNumber(), customer.getCustomer().getMobileNumber(), customer.getAccount().getAccountNumber());
        if (existingCustomer == null) {
            LOGGER.error("Customer account does not exist [" + customer.getGlobalId().getCnicNumber() + "], cannot allow registration, rejecting...");
            throw new ServiceException(String.format("Customer account [%s] does not exist", customer.getGlobalId().getCnicNumber()));
        }

        // Duplicate Customer Check - mobile number
        Customer customerDuplicate = customerRepository.findByMobileNumber(customer.getCustomer().getMobileNumber());
        if (customerDuplicate != null) {
            LOGGER.error("Customer already exists with mobile [" + customer.getCustomer().getMobileNumber() + "], cannot allow registration, rejecting...");
            throw new ServiceException(String.format("Customer with Mobile Number %s already exists", customer.getCustomer().getMobileNumber()));
        }

        // Duplicate Customer Check - account number
        Account accountByAccountNumber = accountRepository.getAccountByAccountNumber(customer.getAccount().getAccountNumber());
        if (accountByAccountNumber != null) {
            LOGGER.error("Customer already exists with account number [" + customer.getAccount().getAccountNumber() + "], cannot allow registration, rejecting...");
            throw new ServiceException(String.format("Customer with account number %s already exists", customer.getAccount().getAccountNumber()));
        }


        return new CustomResponseEntity(existingCustomer, "Response Sent");
    }

    @Override
    @Transactional
    public CustomResponseEntity signup(SignUpDto signUpDto) {
        if (signUpDto == null) {
            throw new ServiceException("SignUp data cannot be null");
        }

        // Validate the SignUpDto
        validateSignUpDto(signUpDto);

        // Validate the image verification

        // Create and set the customer and device entities
        Device device = createDeviceFromDto(signUpDto);

        Customer customer = createCustomerFromDto(signUpDto);
        customer.setDevices(Collections.singletonList(device));
        device.setCustomer(customer);
        // Save the customer (which will also save the device due to cascade settings)
//        deviceRepository.save(device);
        customerRepository.save(customer);
//
//
//        Optional<Customer> c2 = customerRepository.findById(c.getId());
//        c2.get().setDevice(dv);
//        customerRepository.save(c2.get());
        return new CustomResponseEntity<>(customer, "Customer Registered successfully");
    }


    private void validateSignUpDto(SignUpDto signUpDto) {
        boolean cnicExists = customerRepository.existsByCnic(signUpDto.getCnic());
        boolean emailExists = customerRepository.existsByEmail(signUpDto.getEmail());
        boolean userNameExists = customerRepository.existsByUserName(signUpDto.getUserName());
        boolean mobileNumberExists = customerRepository.existsByMobileNumber(signUpDto.getMobileNumber());

        if (cnicExists) {
            throw new ServiceException("An account with this CNIC already exists");
        }
        if (emailExists) {
            throw new ServiceException("An account with this email already exists");
        }
        if (userNameExists) {
            throw new ServiceException("An account with this username already exists");
        }
        if (mobileNumberExists) {
            throw new ServiceException("An account with this mobile number already exists");
        }
    }


    private ImageVerification validateImageVerification(Long securityPictureId) {
        return imageVerificationRepository.findById(securityPictureId)
                .orElseThrow(() -> new ServiceException("Image does not exist"));
    }


    private Customer createCustomerFromDto(SignUpDto signUpDto) {
        Customer customer = new Customer();
        customer.setMobileNumber(signUpDto.getMobileNumber());
        customer.setFirstName(signUpDto.getFirstName());
        customer.setLastName(signUpDto.getLastName());
        customer.setCnic(signUpDto.getCnic());
        customer.setEmail(signUpDto.getEmail());
        customer.setUserName(signUpDto.getUserName());
        customer.setPassword(signUpDto.getPassword());
        customer.setStatus(signUpDto.getStatus());
        customer.setResetToken(signUpDto.getResetToken());
        customer.setResetTokenExpireTime(signUpDto.getResetTokenExpireTime());
        customer.setAccountNumber(signUpDto.getAccountDto().getAccountNumber());

        return customer;
    }

    private Device createDeviceFromDto(SignUpDto signUpDto) {
        Device device = new Device();
        device.setDeviceName(signUpDto.getDevice().getDeviceName()); // Ensure this is set if applicable
        device.setPinHash(signUpDto.getDevice().getPinHash());

        return device;
    }

    @Override
    public CustomResponseEntity loginWithPin(/*Long customerId,*/ String devicePin, String uniquePin) {

//        Customer customer = customerRepository.findByAccountNumber(accountNumber);

//        Customer customer = customerRepository.findCustomerByAccountNumber(accountNumber);

//        Customer customer = customerRepository.findById(customerId).orElse();

        try {
           /* // Check if the customer exists
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ServiceException("Account not found"));*/

//            if (customer == null) {
//                throw new ServiceException("Account not found");
//            }

//            Long findByUnique1 = customer.getId();
//            Device device = deviceRepository.findDevicesByCustomerIdAndDevicePinAndUniquePin(/*customer.getId(), */devicePin, uniquePin);
            Device device;

            if (StringUtils.hasText(devicePin)) {
                // Query for both devicePin and uniquePin
                device = deviceRepository.findByDevicePinAndUniquePin(devicePin, uniquePin);
            } else {
                // Query only by uniquePin
                device = deviceRepository.findByUniquePin(uniquePin);
            }

            if (device == null) {
                return CustomResponseEntity.error("Device not found for this pin");
            }

            Long customerId = device.getCustomer().getId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ServiceException("Account not found"));


//            if (!device.getUnique1().equals(uniquePin)) {
//                return CustomResponseEntity.error("Invalid pin hash");
//            }


            // JWT Implementation Starts
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(customer.getUserName(), customer.getPassword())
            );
            String email = authentication.getName();
            String token = jwtService.generateToken(email);
            LOGGER.info("Token = " + token);
            LOGGER.info("Expiration = " + jwtService.getTokenExpireTime(token).getTime());

            Map<String, Object> data = new HashMap<>();
            data.put("customerId", customer.getId());
            data.put("token", token);
            data.put("expirationTime", jwtService.getTokenExpireTime(token).getTime());

            // Update customer with session token and expiration time
            customer.setSessionToken(token);
            AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("RESET_EXPIRE_TIME"); // fetching token expire time in minutes
            customer.setSessionTokenExpireTime(
                    Long.parseLong(Util.dateFormat.format(
                            DateUtils.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue()))
                    ))
            );
            customerRepository.save(customer);

            return new CustomResponseEntity<>(data, "Customer logged in successfully");

        } catch (ServiceException e) {
            // Handle specific service exceptions
            LOGGER.error("ServiceException occurred: ", e);
            return CustomResponseEntity.error(e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error(e.getMessage());
        }
    }

    @Override
    public CustomResponseEntity deviceRegister(Long id, SettingDTO settingDTO) {

        try {

            String jpql = "SELECT c FROM Device c WHERE c.unique1 = :unique1";
            Map<String, Object> params = new HashMap<>();
            params.put("unique1", settingDTO.getUnique());

            Device device = cardGenericDao.findOneWithQuery(jpql, params);
            if (Objects.nonNull(device)) {
                LOGGER.error("Device already exists");
                return CustomResponseEntity.error("Device already exists");
            }
            if (device == null) {
                String pin = settingDTO.getDevicePin();

                if (!pin.matches("\\d{4}")) {
                    LOGGER.error("Pin must be exactly 4 digits");
                    return CustomResponseEntity.error("Pin must be exactly 4 digits");
                }
                // Check if the pin is sequential (e.g., 1234, 2345, etc.)
                if (isSequential(pin)) {
                    LOGGER.error("Sequential pins are not allowed");
                    return CustomResponseEntity.error("Sequential pins are not allowed");
                }

                Customer customer1 = customerRepository.findById(id).orElseThrow();

                Device dv = new Device();
                dv.setDeviceName(settingDTO.getDeviceName());
                dv.setCustomer(customer1);
                dv.setUnique1(settingDTO.getUnique());
                dv.setDeviceType(settingDTO.getDeviceType());
                dv.setManufacture(settingDTO.getManufacture());
                dv.setModelName(settingDTO.getModelName());
                dv.setOsv_osn(settingDTO.getOsv_osn());
                dv.setDevicePin(settingDTO.getDevicePin());
                dv.setPublic_key(settingDTO.getPublicKey());
                deviceRepository.save(dv);
                LOGGER.error("Device Registered with Customer successfully...");
                return new CustomResponseEntity<>(dv.getId(), "Device Registered with Customer successfully...");
            }

        } catch (EntityNotFoundException e) {
            // Handle case where the device is not found
            LOGGER.error("EntityNotFoundException occurred: ", e);
            return CustomResponseEntity.error(e.getMessage() + " Failed Register Device! ");
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error(e.getMessage() + " Failed Register Device!");
        }
        return null;
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
    public CustomResponseEntity fetchDeviceRegister(String customerId) {
        String jpql = "SELECT d FROM Device d WHERE d.customer.id = :customerId";
        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);

        try {
            // Fetching the list of devices
            List<Device> devices = cardGenericDao.findWithQuery(jpql, params);

            // Check if devices list is empty
            if (devices.isEmpty()) {
                LOGGER.info("Error Receiving Device Details With Customer ID: " + customerId);
                return CustomResponseEntity.error("No devices found with the given Customer ID");
            }

            // Convert Device entities to DeviceDetailsDto
            List<DeviceDetailDto> deviceDetailDtos = devices.stream().map(device -> {
                DeviceDetailDto dto = new DeviceDetailDto();
                dto.setDeviceId(device.getId());
                dto.setDeviceName(device.getDeviceName());
                dto.setPinHash(device.getPinHash());
                dto.setDevicePin(device.getDevicePin());
                dto.setDeviceType(device.getDeviceType());
                dto.setUnique(device.getUnique1());
                dto.setOsv_osn(device.getOsv_osn());
                dto.setModelName(device.getModelName());
                dto.setManufacture(device.getManufacture());
                dto.setOsv_osn(device.getOs_name());
                dto.setPublicKey(device.getPublic_key());
                dto.setPinStatus(String.valueOf(device.getPinStatus())); // Assuming PinStatus is an enum
                dto.setDateAndTime(String.valueOf(device.getTimestamp())); // Assuming you have a timestamp field
                return dto;
            }).collect(Collectors.toList());

            // Return the response with the list of device details
            return new CustomResponseEntity<>(deviceDetailDtos, "Device Details");

        } catch (Exception e) {
            LOGGER.error("Error fetching device details for Customer ID: " + customerId, e);
            return CustomResponseEntity.error("An error occurred while fetching device details: " + e.getMessage());
        }
    }

    @Override
    public CustomResponseEntity deleteDevice(Long deviceId) {
        Optional<Device> device = Optional.ofNullable(deviceRepository.findById(deviceId).orElseThrow(() -> new ServiceException("Invalid Device Id")));
        if(device.isPresent()){
            deviceRepository.deleteById(deviceId);
            return new CustomResponseEntity(device," is deleted Successfully");
        }
        return CustomResponseEntity.error("Invalid device id ");
    }
//    private void associateCustomerWithDevice(Customer customer, Device device) {
    ////        customer.setDevice(device);
    //        device.setCustomer(customer);
    //    }


//    public boolean verifyDevice(String deviceId, String verificationCode) {
//        Device device = deviceRepository.findByDeviceId(deviceId);
//
//        if (device != null && device.getVerificationCode().equals(verificationCode)) {
//            device.setVerified(true);
//            device.setUpdatedAt(LocalDateTime.now());
//            deviceRepository.save(device);
//            return true;
//        }
//        return false;
//    }
//
//    public void setPin(String deviceId, String pin) {
//        Device device = deviceRepository.findByDeviceId(deviceId);
//
//        if (device != null && device.getVerified()) {
//            String pinHash = passwordEncoder.encode(pin);
//            device.setPinHash(pinHash);
//            device.setPinSet(true);
//            device.setUpdatedAt(LocalDateTime.now());
//            deviceRepository.save(device);
//        } else {
////            throw CustomResponseEntity.error;
//        }
//    }
//
//    public boolean validatePin(String deviceId, String pin) {
//        Device device = deviceRepository.findByDeviceId(deviceId);
//
//        return device != null && device.getPinSet() && passwordEncoder.matches(pin, device.getPinHash());
//    }
//
//    private String generateVerificationCode() {
//        return String.format("%06d", (int) (Math.random() * 1000000));
//    }
}

