package com.iconsult.controller;

import com.iconsult.model.NotificationEvent;
import com.iconsult.service.NotificationProducerService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/notification")
public class NotificationController {
    @Autowired
    private NotificationProducerService notificationProducerService;

    @PostMapping("/process-notification")
    public CustomResponseEntity processNotification (@RequestBody NotificationEvent notification){
        return notificationProducerService.sendNotification(notification);
    }

}
