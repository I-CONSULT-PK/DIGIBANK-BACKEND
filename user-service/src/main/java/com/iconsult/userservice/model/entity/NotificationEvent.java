package com.iconsult.userservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private Long recipientId;
    private String email;
    private String notificationType;
    private String message;
    private Timestamp timeStamp;
    private String channel;
}
