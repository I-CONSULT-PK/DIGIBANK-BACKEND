package com.example.Quartz.service.impl;

import com.example.Quartz.config.MobTopUpJobExecutor;
import com.example.Quartz.model.dto.request.ScheduleMobileTopUpPaymentRequest;
import com.example.Quartz.model.entity.ScheduleMobileTopUpPayment;
import com.example.Quartz.repository.MobileTopUpPaymentRepository;
import com.example.Quartz.service.MobileTopUpQuartzService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


@Service
public class MobileTopUpQuartzServiceImpl implements MobileTopUpQuartzService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileTopUpQuartzServiceImpl.class);


    @Autowired
    private Scheduler scheduler;

    @Autowired
    MobileTopUpPaymentRepository mobileTopUpPaymentRepository;

    @Override
    public CustomResponseEntity scheduleMobileTopUp(ScheduleMobileTopUpPaymentRequest scheduleMobileTopUpPaymentRequest, String bearerToken) {
        try {
            ScheduleMobileTopUpPayment scheduleMobileTopUpPayment = new ScheduleMobileTopUpPayment();
            scheduleMobileTopUpPayment.setAccountNumber(scheduleMobileTopUpPaymentRequest.getAccountNumber());
            scheduleMobileTopUpPayment.setMobileNumber(scheduleMobileTopUpPaymentRequest.getMobileNumber());
            scheduleMobileTopUpPayment.setPackageId(scheduleMobileTopUpPaymentRequest.getPackageId());
            scheduleMobileTopUpPayment.setMobileTopUpPaymentDate(scheduleMobileTopUpPaymentRequest.getLocalDate().toString());
            scheduleMobileTopUpPayment.setStatus("In Progress");
            ScheduleMobileTopUpPayment scheduleBill= mobileTopUpPaymentRepository.save(scheduleMobileTopUpPayment);
            scheduleMobileTopUpPaymentRequest.setScheduledId(scheduleBill.getId());
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("scheduleMobileTopUpPaymentRequest", scheduleMobileTopUpPaymentRequest);

            JobDetail job = newJob(MobTopUpJobExecutor.class)
                    .withIdentity("jobIdentity-"+scheduleBill.getId())
                    .usingJobData(jobDataMap)
                    .build();
            Date startAt = Date.from(scheduleMobileTopUpPaymentRequest.getLocalDate().atZone(ZoneId.systemDefault()).toInstant());
            Trigger trigger = newTrigger()
                    .withIdentity("triggerIdentity-"+scheduleBill.getId())
                    .startAt(startAt)
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60)
                            .withRepeatCount(1)
                            .withMisfireHandlingInstructionNextWithRemainingCount()
                    )
                    .build();
            scheduler.scheduleJob(job, trigger);
            LOGGER.info("Mobile Top Up Payment Scheduled");
            LOGGER.info("Mobile Top Up Payment Scheduled");
            return new CustomResponseEntity(scheduleMobileTopUpPaymentRequest,"Success");
        } catch (Exception exception) {
            LOGGER.info("Mobile Top Up Payment Failed {}", exception.getMessage());
            return CustomResponseEntity.error("Mobile Top Up Payment Failed");
        }
    }
}
