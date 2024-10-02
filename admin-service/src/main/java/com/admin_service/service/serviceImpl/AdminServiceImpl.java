package com.admin_service.service.serviceImpl;

import com.admin_service.config.MyUserDetailsService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

}