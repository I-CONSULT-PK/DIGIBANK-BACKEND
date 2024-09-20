package com.iconsult.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String recipientId;
    private String email;
    private String notificationType;
    private String message;
    private LocalDateTime timeStamp;
    private String channel;
}
