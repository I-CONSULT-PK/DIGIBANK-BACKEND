package com.admin_service.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateModuleDto {
    private Integer id;
    private String moduleCode;
    private String modulesTxt;
    private String moduleTxt;
    private String moduleLtxt;
    private Integer clientId;  // Use the same name as the column in the database
    private Date sysDateTime;

}
