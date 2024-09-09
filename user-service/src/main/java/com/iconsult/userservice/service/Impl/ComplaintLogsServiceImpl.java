package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.model.dto.request.ComplainLogsDto;
import com.iconsult.userservice.model.entity.ComplaintsLogs;
import com.iconsult.userservice.service.ComplaintsLogsService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComplaintLogsServiceImpl implements ComplaintsLogsService {
   private static final Logger logger = LoggerFactory.getLogger(ComplaintLogsServiceImpl.class);
    @Autowired
    GenericDao<ComplaintsLogs> complainLogsGenericDao;
    private ComplaintsLogs complaintsLogs;
    @Override
    public CustomResponseEntity fetchAllComplaintServices() {
         List<ComplaintsLogs> complaintsLogsList = complainLogsGenericDao.findAll(ComplaintsLogs.class);
         logger.info("fetch complaints List : "+complaintsLogsList);
         return new CustomResponseEntity(complaintsLogsList,"Success");
    }

    @Override
    public CustomResponseEntity addComplaint(ComplainLogsDto complaintsLogsRequest) {
        ComplaintsLogs complaintsLogs = new ComplaintsLogs();
        complaintsLogs.setComplaintType(capitalizeFirstLetter(complaintsLogsRequest.getComplaintType()));
        complaintsLogs.setReceive(0);
        complaintsLogs.setClosed(0);
        ComplaintsLogs save = (ComplaintsLogs) fetchSpecificComplaint(complaintsLogsRequest.getComplaintType()).getData();
        if(save!=null && save.getComplaintType().equals(complaintsLogsRequest.getComplaintType())){
            logger.info("Complaints type already exist : "+complaintsLogsRequest.getComplaintType());
            return CustomResponseEntity.error("This Complaint Type Already Exist");
        }
        complainLogsGenericDao.saveOrUpdate(complaintsLogs);
        logger.info(complaintsLogs.getComplaintType()+" is save");
        return new CustomResponseEntity<>(complaintsLogs,"Success");
    }

    @Override
    public CustomResponseEntity fetchSpecificComplaint(String service) {
        service = capitalizeFirstLetter(service);
        Map<String, Object> param = new HashMap<>(Map.of("service",service));
        String jpql = "Select c from ComplaintsLogs c where c.complaintType = :service";
        ComplaintsLogs complaintsLogs = complainLogsGenericDao.findOneWithQuery(jpql,param);
        return new CustomResponseEntity<>(complaintsLogs,"Success");
    }
    public static String capitalizeFirstLetter(String input) {
        return Arrays.stream(input.toLowerCase().split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
