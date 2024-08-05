package com.iconsult.userservice.model.mapper;

import com.iconsult.userservice.model.dto.request.DeviceDto;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.Device;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeviceMapper {

    public Device toEntity(DeviceDto dto, Customer customer) {
        Device device = new Device();
        device.setCustomer(customer);
//        device.setDeviceId(dto.getDeviceId());
        device.setDeviceName(dto.getDeviceName());
//        device.setVerificationCode(dto.getVerificationCode());
//        device.setCreatedAt(LocalDateTime.now());
//        device.setUpdatedAt(LocalDateTime.now());
        return device;
    }

    public DeviceDto toDto(Device device) {
        DeviceDto dto = new DeviceDto();
        dto.setCustomerId(device.getCustomer().getId());
//        dto.setDeviceId(device.getDeviceId());
        dto.setDeviceName(device.getDeviceName());
//        dto.setVerificationCode(device.getVerificationCode());
        dto.setPin(device.getPinHash());
        return dto;
    }
}
