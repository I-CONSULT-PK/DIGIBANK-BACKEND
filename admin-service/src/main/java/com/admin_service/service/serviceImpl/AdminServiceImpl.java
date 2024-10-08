package com.admin_service.service.serviceImpl;

import com.admin_service.config.MyUserDetailsService;
import com.admin_service.dto.request.AddUserDto;
import com.admin_service.dto.request.LoginDto;
import com.admin_service.entity.User;
import com.admin_service.entity.AppConfiguration;
import com.admin_service.model.CustomResponseEntity;

import com.admin_service.repository.UserRepository;
import com.admin_service.service.AdminService;
import com.admin_service.service.JwtService;
import com.admin_service.util.Util;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private JwtUtil jwtUtil;

    private CustomResponseEntity response;

    @Autowired
    private AppConfigurationImpl appConfigurationImpl;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Override
    public CustomResponseEntity login(LoginDto loginDto) {
        User user = userRepository.findByUserName(loginDto.getEmailorUsername());

        // Check if user exists
        if (user == null) {
            throw new ServiceException("User not found");
        }

        // Validate password using PasswordEncoder
//        PasswordEncoder passwordEncoder = passwordEncoder(); // Get your PasswordEncoder bean
//        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
//            throw new ServiceException("Invalid Password");
//        }

        // JWT Implementation Starts
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmailorUsername(), loginDto.getPassword())
        );

        String email = authentication.getName();

        // Fetch user roles from the database
        Set<GrantedAuthority> roles = myUserDetailsService.getAuthorities(user); // Assuming you have a method to get roles

        // Generate the token with roles
        String token = jwtUtil.generateToken(email, roles);
        LOGGER.info("Token = " + token);
        Date expirationDate = new Date(jwtUtil.getTokenExpireTime(token).getTime());
        LOGGER.info("Expiration = " + jwtUtil.getTokenExpireTime(token).getTime());
        LOGGER.info("Expiration Date and Time with a Specific DateTime Format : "+expirationDate);
        // JWT Implementation Ends

        // Prepare response data
        Map<String, Object> data = new HashMap<>();
        data.put("adminId", user.getId());
        data.put("token", token);
        data.put("expirationTime", jwtUtil.getTokenExpireTime(token).getTime());

        // Create response entity
        CustomResponseEntity response = new CustomResponseEntity<>(data, "login successfully");

        // Set session token if necessary (optional for JWT)
        user.setSessionToken(token); // Only if you need to store it
        AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("RESET_EXPIRE_TIME"); // Fetch token expiration time in minutes
        user.setSessionTokenExpireTime(Long.parseLong(Util.dateFormat.format(DateUtils.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue())))));

        // Update user (ensure you're not saving an unnecessary token if using stateless JWT)
        updateAdmin(user);

        return response;
    }
//
//        User user = userRepository.findByUserName(loginDto.getEmailorUsername());
//
//            if (user.getPassword().equals(loginDto.getPassword())) {
//                // JWT Implementation Starts
//                Authentication authentication =
//                        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailorUsername(), loginDto.getPassword()));
//                String email = authentication.getName();
//                String token = jwtUtil.generateToken(email,loginDto.);
//                //String token = jwtService.generateToken(email);
//                LOGGER.info("Token = " + token);
//                LOGGER.info("Expiration = " +  jwtUtil.getTokenExpireTime(token).getTime());
//                // JWT Implementation Ends
//
//                Map<String, Object> data = new HashMap<>();
//                data.put("adminId", user.getId());
//                data.put("token", token);
//                data.put("expirationTime", jwtService.getTokenExpireTime(token).getTime());
//                response = new CustomResponseEntity<>(data, "admin logged in successfully");
//                //set customer token
//                user.setSessionToken(token);
//                AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("RESET_EXPIRE_TIME"); // fetching token expire time in minutes
//                user.setSessionTokenExpireTime(Long.parseLong(Util.dateFormat.format(DateUtils.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue())))));
//                updateAdmin(user);
//
//                return response;
//            } else {
//                throw new ServiceException("Invalid Password ");
//            }
//
//        }

    @Override
    public User updateAdmin(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public CustomResponseEntity addUser(AddUserDto addUserDto) {
        if (addUserDto == null) {
            LOGGER.info("data cannot be null");
            return CustomResponseEntity.error("data cannot be null");
        }
        User user = new User();
        if (addUserDto.getUserName()==null || addUserDto.getFirstName()==null || addUserDto.getLastName() == null
        || addUserDto.getEmail() == null || addUserDto.getPassword() == null || addUserDto.getUserName().isBlank() ||
        addUserDto.getPassword().isBlank() || addUserDto.getFirstName().isBlank() || addUserDto.getLastName().isBlank() ||
        addUserDto.getEmail().isBlank() || addUserDto.getJobType().isBlank() || addUserDto.getJobType()==null) {
            LOGGER.info("data cannot be null");
            return CustomResponseEntity.error("data cannot be null");
        }
        if(addUserDto.getMultiTenant()== true) {
            if(addUserDto.getCountry() == null || addUserDto.getCountry().isBlank()) {
                LOGGER.info("country cannot be null");
                return CustomResponseEntity.error("country cannot be null");
            }
            user.setMultiTenant(addUserDto.getMultiTenant());
            user.setCountry(addUserDto.getCountry());

        } else {
            user.setMultiTenant(false);
        }
        if(addUserDto.getActivation().equals("temporary")) {
            if(addUserDto.getFromDuration()==null ||addUserDto.getToDuration()==null) {
                LOGGER.info("User from or to duration cannot be null");
                return CustomResponseEntity.error("User from or to duration cannot be null");
            }
            user.setFromDuration(addUserDto.getFromDuration());
            user.setToDuration(addUserDto.getToDuration());
            user.setActivation("temporary");
        } else {
            user.setActivation("Permanent");
        }

        // Check for existing data

        if (userRepository.existsByEmail(addUserDto.getEmail())) {
            LOGGER.info("User Already Exist");
            return CustomResponseEntity.error("User Already Exist");
        }

        if (userRepository.existsByUserName(addUserDto.getUserName())) {
            LOGGER.info("User Already Exist");
            return CustomResponseEntity.error("User Already Exist");
        }


        user.setFirstName(addUserDto.getFirstName());
        user.setLastName(addUserDto.getLastName());
        user.setEmail(addUserDto.getEmail());
        user.setUserName(addUserDto.getUserName());
        user.setPassword(addUserDto.getPassword());
        user.setJobType(addUserDto.getJobType());
        user.setStatus(true);
        userRepository.save(user);
        LOGGER.info("User created successfully");
        return new CustomResponseEntity<>(user,"User created successfully");

    }
}