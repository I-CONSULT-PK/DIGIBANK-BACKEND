package com.iconsult.userservice.model.dto.request;

import com.iconsult.userservice.model.entity.Device;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DeviceDetailDto {

    private Long deviceId;
    private Long customerId;
    private String deviceName;
    private String devicePin;
    private String deviceType;
    private String unique;
    private String osv_osn;
    private String modelName;
    private String manufacture;

    // Constructor, getters, and setters
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
    }

}
