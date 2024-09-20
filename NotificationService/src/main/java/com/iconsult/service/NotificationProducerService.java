package com.iconsult.service;

import com.iconsult.model.NotificationEvent;
import com.zanbeel.customUtility.model.CustomResponseEntity;

public interface NotificationProducerService {


    CustomResponseEntity sendNotification (NotificationEvent event);
}
