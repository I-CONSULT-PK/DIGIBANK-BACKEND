package com.iconsult.userservice.service;

import java.io.Serializable;

public interface UserActivitiesService extends Serializable {

    public void deleteOldUserActivityRecordsAfterThirtyDays();
}
