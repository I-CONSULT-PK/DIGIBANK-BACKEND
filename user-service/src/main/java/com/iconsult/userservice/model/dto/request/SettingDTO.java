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

    @Pattern(regexp = "\\d+", message = "New Pin must be numeric")
    private String devicePin;

    @Pattern(regexp = "\\d+", message = "Old Pin must be numeric")
    private String oldPin;

    private String deviceType;
    private String unique;
    private String osv_osn;
    private String modelName;
    private String manufacture;
}
