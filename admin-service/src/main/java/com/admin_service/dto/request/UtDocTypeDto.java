package com.admin_service.dto.request;

import com.admin_service.entity.HdrAdModule;
import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UtDocTypeDto {
    private Integer id;

    private String doctypecode;
    private String doctypestxt;
    private Integer docTypeClientId;
    private Date sysdatetime;
    private Long modulelid;


}
