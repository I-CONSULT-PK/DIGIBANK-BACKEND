package com.iconsult.userservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private int event_id;
    private String event_type;
    private Timestamp timestamp;
    private String customerId;
    private String email;
    private String message;
}
