package com.iconsult.userservice.model.dto.request;

import com.iconsult.userservice.model.entity.Device;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceDetailDto {

    private Long deviceId;
    private Long customerId;
    private String deviceName;
    private String devicePin;

    private String pinHash;
    private String deviceType;
    private String unique;
    private String osv_osn;
    private String modelName;
    private String manufacture;

    private String pinStatus;
    private String dateAndTime;

    private String publicKey;


    // Default constructor
    public DeviceDetailDto() {
    }
    // Constructor to initialize from Device entity
    public DeviceDetailDto(Device device) {
        this.deviceId = device.getId();
        this.customerId = device.getCustomer() != null ? device.getCustomer().getId() : null;
        this.deviceName = device.getDeviceName();
        this.devicePin = device.getDevicePin();
        this.deviceType = device.getDeviceType();
        this.unique = device.getUnique1();
        this.osv_osn = device.getOsv_osn();
        this.modelName = device.getModelName();
        this.manufacture = device.getManufacture();
        this.pinStatus = String.valueOf(device.getPinStatus());
        this.dateAndTime = String.valueOf(device.getTimestamp());
    }


}
