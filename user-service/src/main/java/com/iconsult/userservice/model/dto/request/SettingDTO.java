package com.iconsult.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SettingDTO {

    private String deviceName;

    @Pattern(regexp = "\\d{4}", message = "Pin must be exactly 4 digits")
    private String devicePin;

//    @Pattern(regexp = "\\d{4}+", message = "Pin must be exactly 4 digits")
    private String oldPin;

    private String deviceType;
    private String unique;
    private String osv_osn;
    private String modelName;
    private String manufacture;
}
