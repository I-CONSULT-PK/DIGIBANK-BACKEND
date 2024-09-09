package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.GenericDao.GenericDao;
import com.iconsult.userservice.dto.EmailDto;
import com.iconsult.userservice.model.dto.request.ComplaintDto;
import com.iconsult.userservice.model.entity.Complaint;
import com.iconsult.userservice.model.entity.ComplaintsLogs;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.service.ComplaintService;
import com.iconsult.userservice.service.CustomerService;
import com.iconsult.userservice.service.EmailService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
@Service
public class ComplaintServiceImpl implements ComplaintService {
    private Logger logger = LoggerFactory.getLogger(ComplaintServiceImpl.class);
    private Complaint complaint;
    @Autowired
    private GenericDao<Complaint> complaintGenericDao;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ComplaintLogsServiceImpl complaintLogsService;
    @Autowired
    private GenericDao<ComplaintsLogs> complaintsLogsGenericDao;
    @Autowired
    EmailService emailService;
    private Customer fetchCustomer(Long customerId){
        return  (Customer) customerService.findById(customerId).getData();
    }
    @Override
    public CustomResponseEntity createComplaint(ComplaintDto complaintDto) {
        Complaint complaint = new Complaint();
        RandomNumberGeneratorImp randomNumberGeneratorImp = new RandomNumberGeneratorImp();
        complaint.setComplainNumber(randomNumberGeneratorImp.generateUniqueNumber(6));
        complaint.setDescription(complaintDto.getSummary());
        complaint.setComplainOpenDate(LocalDateTime.now().toString());
        Customer customer = fetchCustomer(complaintDto.getCustomerId());

        if(Objects.isNull(customer)){
            logger.error("invalid Customer Id");
            return CustomResponseEntity.error("Customer does not exist");
        }
        complaint.setCustomerId(customer);
        ComplaintsLogs complaintsLogs = getComplaintsLogs(ComplaintLogsServiceImpl.capitalizeFirstLetter(complaintDto.getServices()));
        if(complaintsLogs == null){
            logger.error("Invalid Complaints Type ");
            return CustomResponseEntity.error("Invalid Complaint Type");
        }
        complaint.setComplaintType(complaintsLogs);;
        complaintGenericDao.saveOrUpdate(complaint);
        EmailDto emailDto = new EmailDto();
        emailDto.setRecipient(customer.getEmail());
        emailDto.setSubject("Complaint Number");
        emailDto.setBody("Your grievance has been received and will revert to you. Your auto generated ticket number is "+complaint.getComplainNumber());
        emailService.sendSimpleMessage(emailDto);
        logger.info("Complaints Number Send");
        incrementCompliant(complaintsLogs);
        return new CustomResponseEntity<>("Your grievance has been received. " +
                "You will get a complain number within 24 to 48 hours.");
    }
    private Object incrementCompliant(ComplaintsLogs complaintsLogs){
        //working on it 05/09/2004
        if(Objects.isNull(complaintsLogs)){
            return CustomResponseEntity.error("Invalid Complaint-type");
        }
        int count = complaintsLogs.getReceive();
        complaintsLogs.setReceive(count+1);
        return complaintsLogsGenericDao.saveOrUpdate(complaintsLogs);
    }

    @Override
    public CustomResponseEntity showComplaint(Long customerId) {
        Customer customer = fetchCustomer(customerId);
        if (customer == null) {
            logger.error("Invalid Customer Id");
            return CustomResponseEntity.error("Invalid Customer Id");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("customer",customer);
        String jpql = "Select c from Complaint c where customerId = :customer";
        List<Complaint> complaintList = complaintGenericDao.findWithQuery(jpql,params);
        logger.info("Show Complaints List");
        return new CustomResponseEntity<>(complaintList,"Success");
    }
    //List<Complaint> getComplainReference();
    private ComplaintsLogs getComplaintsLogs(String complainType){
        if (complainType==null){
            return null;
        }
        CustomResponseEntity complaintType = complaintLogsService.fetchSpecificComplaint(complainType);
        return (ComplaintsLogs) complaintType.getData();}
}
