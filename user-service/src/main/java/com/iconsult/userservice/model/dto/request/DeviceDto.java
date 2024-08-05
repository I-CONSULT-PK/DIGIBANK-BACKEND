package com.iconsult.userservice.model.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DeviceDto {
    private Long customerId;
    private String deviceId;
    private String deviceName;
    private String verificationCode;
    private String pin;

}
