package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.Util.Util;
import com.iconsult.userservice.model.dto.request.SignUpDto;
import com.iconsult.userservice.model.entity.*;
import com.iconsult.userservice.model.mapper.DeviceMapper;
import com.iconsult.userservice.exception.ServiceException;
import com.iconsult.userservice.model.dto.request.DeviceDto;
import com.iconsult.userservice.model.dto.response.SignUpResponse;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.repository.DeviceRepository;
import com.iconsult.userservice.repository.ImageVerificationRepository;
import com.iconsult.userservice.service.DeviceService;
import com.iconsult.userservice.service.EmailService;
import com.iconsult.userservice.service.JwtService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    private CustomResponseEntity response;

    @Autowired
    ImageVerificationRepository imageVerificationRepository;
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
        ImageVerification imageVerification = validateImageVerification(signUpDto.getSecurityPictureId());

        // Create and set the customer and device entities
        Customer customer = createCustomerFromDto(signUpDto, imageVerification);
        Device device = createDeviceFromDto(signUpDto);

        // Save the customer (which will also save the device due to cascade settings)
        Customer c = customerRepository.save(customer);
        device.setCustomer(c);
        Device dv = deviceRepository.save(device);
        Optional<Customer> c2 = customerRepository.findById(c.getId());
        c2.get().setDevice(dv);
        customerRepository.save(c2.get());
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


    private Customer createCustomerFromDto(SignUpDto signUpDto, ImageVerification imageVerification) {
        Customer customer = new Customer();
        customer.setMobileNumber(signUpDto.getMobileNumber());
        customer.setFirstName(signUpDto.getFirstName());
        customer.setLastName(signUpDto.getLastName());
        customer.setCnic(signUpDto.getCnic());
        customer.setEmail(signUpDto.getEmail());
        customer.setUserName(signUpDto.getUserName());
        customer.setPassword(signUpDto.getPassword());
        customer.setSecurityPicture(imageVerification.getName());
        customer.setStatus(signUpDto.getStatus());
        customer.setResetToken(signUpDto.getResetToken());
        customer.setResetTokenExpireTime(signUpDto.getResetTokenExpireTime());
        customer.setAccountNumber(signUpDto.getAccountDto().getAccountNumber());
        customer.setDevice(signUpDto.getDevice());

        return customer;
    }

    private Device createDeviceFromDto(SignUpDto signUpDto) {
        Device device = new Device();
        device.setDeviceName(signUpDto.getDevice().getDeviceName()); // Ensure this is set if applicable
        device.setPinHash(signUpDto.getDevice().getPinHash());

        return device;
    }

    @Override
    public CustomResponseEntity getPinHashByAccountNumberAndPinHash(String accountNumber, String pinHash) {
        // Check if the customer exists
        Customer customer = customerRepository.findByAccountNumber(accountNumber);

        if (customer == null) {
            throw new ServiceException("Account not found");
        }

        // Check if the device exists and the pin hash matches
        Device device = customer.getDevice();
        if (device == null) {
            throw new ServiceException("Device not found for this customer");
        }

        // Validate the provided pinHash against the stored pinHash
        String storedPinHash = device.getPinHash();
        if (!storedPinHash.equals(pinHash)) {
            throw new ServiceException("Invalid pin hash");
        }

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
    }
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


