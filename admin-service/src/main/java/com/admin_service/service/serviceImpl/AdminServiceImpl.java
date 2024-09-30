package com.admin_service.service.serviceImpl;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    private CustomResponseEntity response;

    @Autowired
    private AppConfigurationImpl appConfigurationImpl;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

//    @Override
//    public CustomResponseEntity login(LoginDto loginDto) {
//
//        User user = userRepository.findByUserName(loginDto.getEmailorUsername());
//
//            if (user.getPassword().equals(loginDto.getPassword())) {
//                // JWT Implementation Starts
//                Authentication authentication =
//                        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailorUsername(), loginDto.getPassword()));
//                String email = authentication.getName();
//
//
//                String token = jwtService.generateToken(email);
//                LOGGER.info("Token = " + token);
//                LOGGER.info("Expiration = " + jwtService.getTokenExpireTime(token).getTime());
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
    public CustomResponseEntity login(LoginDto loginDto) {
        User user = userRepository.findByUserName(loginDto.getEmailorUsername());

        if (user != null && user.getPassword().equals(loginDto.getPassword())) {
            // JWT Implementation Starts
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailorUsername(), loginDto.getPassword()));
            String email = authentication.getName();

            // Set the role in the token
            Set<GrantedAuthority> roles = new HashSet<>();
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            String token = jwtService.generateToken(email, roles);

            LOGGER.info("Token = " + token);
            LOGGER.info("Expiration = " + jwtService.getTokenExpireTime(token).getTime());
            // JWT Implementation Ends

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("token", token);
            data.put("expirationTime", jwtService.getTokenExpireTime(token).getTime());
            response = new CustomResponseEntity<>(data, "admin logged in successfully");

            // Set customer token
            user.setSessionToken(token);
            AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("RESET_EXPIRE_TIME"); // fetching token expire time in minutes
            user.setSessionTokenExpireTime(Long.parseLong(Util.dateFormat.format(DateUtils.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue())))));

            updateAdmin(user);

            return response;
        } else {
            throw new ServiceException("Invalid Password");
        }
    }



    @Override
    public User updateAdmin(User user) {
        return this.userRepository.save(user);
    }

}