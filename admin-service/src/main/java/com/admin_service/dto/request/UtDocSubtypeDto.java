package com.admin_service.dto.request;

import com.admin_service.entity.HdrAdModule;
import com.admin_service.entity.UtDocTypeSetup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UtDocSubtypeDto {
    private Integer id;
    private String docsubtypecode;
    private String docsubtypestxt;
    private Integer clientId;
    private Date sysdatetime;
    private UtDocTypeSetup doctypeid;
    private HdrAdModule modulelid;
}
