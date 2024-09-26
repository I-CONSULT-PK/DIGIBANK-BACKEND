package com.admin_service.service.serviceImpl;

import com.admin_service.dto.request.LoginDto;
import com.admin_service.entity.Admin;
import com.admin_service.entity.AppConfiguration;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.repository.AdminRepository;
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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private CustomResponseEntity response;

    @Autowired
    private AppConfigurationImpl appConfigurationImpl;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Override
    public CustomResponseEntity login(LoginDto loginDto) {

        Admin admin = adminRepository.findByEmailOrUserName(loginDto.getEmailorUsername());

            if (admin.getPassword().equals(loginDto.getPassword())) {
                // JWT Implementation Starts
                Authentication authentication =
                        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailorUsername(), loginDto.getPassword()));
                String email = authentication.getName();
                String token = jwtService.generateToken(email);
                LOGGER.info("Token = " + token);
                LOGGER.info("Expiration = " + jwtService.getTokenExpireTime(token).getTime());
                // JWT Implementation Ends

                Map<String, Object> data = new HashMap<>();
                data.put("adminId", admin.getId());
                data.put("token", token);
                data.put("expirationTime", jwtService.getTokenExpireTime(token).getTime());
                response = new CustomResponseEntity<>(data, "admin logged in successfully");
                //set customer token
                admin.setSessionToken(token);
                AppConfiguration appConfiguration = this.appConfigurationImpl.findByName("RESET_EXPIRE_TIME"); // fetching token expire time in minutes
                admin.setSessionTokenExpireTime(Long.parseLong(Util.dateFormat.format(DateUtils.addMinutes(new Date(), Integer.parseInt(appConfiguration.getValue())))));
                updateAdmin(admin);

                return response;
            } else {
                throw new ServiceException("Invalid Password ");
            }

        }

    @Override
    public Admin updateAdmin(Admin admin) {
        return this.adminRepository.save(admin);
    }

}