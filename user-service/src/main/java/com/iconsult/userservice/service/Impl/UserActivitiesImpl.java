package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.service.UserActivitiesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserActivitiesImpl implements UserActivitiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserActivitiesImpl.class);

    @PersistenceContext
    private EntityManager entityManager;


    @Scheduled(cron = "*/10 * * * * ?") // Every 10 seconds for testing
//    @Scheduled(cron = "0 * * * * ?") // Every minute
//    @Scheduled(cron = "0 0 0 * * ?")  // Runs daily at midnight
    public void deleteOldUserActivityRecordsAfterThirtyDays() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LOGGER.info(String.valueOf(LocalDateTime.now().minusDays(30)));
        LOGGER.info("Scheduled task started at " + LocalDateTime.now());

        try {
            // Count records older than 30 days
            String countJpql = "SELECT COUNT(u) FROM UserActivity u WHERE u.activityDate < :dateTime";
            Query countQuery = entityManager.createQuery(countJpql);
            countQuery.setParameter("dateTime", thirtyDaysAgo);
            long count = (long) countQuery.getSingleResult();

            if (count > 0) {
                LOGGER.info("There are " + count + " records older than 30 days.");
            } else {
                LOGGER.info("No records older than 30 days.");
            }

            // Delete old records after than 30 days
            String deleteJpql = "DELETE FROM UserActivity u WHERE u.activityDate < :dateTime";
            Query deleteQuery = entityManager.createQuery(deleteJpql);
            deleteQuery.setParameter("dateTime", thirtyDaysAgo);
            int deletedCount = deleteQuery.executeUpdate();

            LOGGER.info("Deleted " + deletedCount + " records older than 30 days.");
        } catch (Exception e) {
            LOGGER.info("An error occurred while deleting old user activity records: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
}
