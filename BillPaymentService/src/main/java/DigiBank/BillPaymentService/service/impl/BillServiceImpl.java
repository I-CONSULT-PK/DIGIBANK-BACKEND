package DigiBank.BillPaymentService.service.impl;

import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import DigiBank.BillPaymentService.constants.Util;
import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.dto.UtilityTypeDto;
import DigiBank.BillPaymentService.model.dto.request.BillDto;
import DigiBank.BillPaymentService.model.dto.request.BillDtoResponse;
import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.model.entity.Account;
import DigiBank.BillPaymentService.model.entity.Bill;
import DigiBank.BillPaymentService.model.entity.Biller;
import DigiBank.BillPaymentService.model.mapper.BillerMapper;
import DigiBank.BillPaymentService.repository.AccountRepository;
import DigiBank.BillPaymentService.repository.BillRepository;
import DigiBank.BillPaymentService.repository.BillerRepository;
import DigiBank.BillPaymentService.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillerRepository billerRepository;

    @Autowired
    private BillerMapper billerMapper;
    @Override
    public CustomResponseEntity addBiller(BillerDtoRequest request) {

        if (request.getUtilityType() == null) {
            return CustomResponseEntity.error( "Utility type must not be null.");
        }

        if (!isValidUtilityType(request.getUtilityType())) {
            return CustomResponseEntity.error ("Invalid utility type: " + request.getUtilityType());
        }
        if (billerRepository.findByUtilityType(request.getUtilityType()).isPresent()) {
            return CustomResponseEntity.error("A biller with the utility type " + request.getUtilityType() + " already exists.");
        }

        Biller biller = billerMapper.dtoToEntity(request);
        String serviceCode = Util.generateUniqueServiceCode(request.getName());
        biller.setServiceCode(serviceCode);
        billerRepository.save(biller);



        return new CustomResponseEntity(request, "biller added!");
    }
    private boolean isValidUtilityType(UtilityType utilityType) {

        for (UtilityType type : UtilityType.values()) {
            if (type == utilityType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CustomResponseEntity getBillDetailsByConsumerNumber(String consumerNumber, String serviceCode, String utilityTypeString) {
        // Validate input parameters
        if (consumerNumber == null || consumerNumber.trim().isEmpty()) {
            return CustomResponseEntity.error("Consumer number must not be null or empty.");
        }

        if (serviceCode == null || serviceCode.trim().isEmpty()) {
            return CustomResponseEntity.error("Service code must not be null or empty.");
        }

        if (utilityTypeString == null || utilityTypeString.trim().isEmpty()) {
            return CustomResponseEntity.error("Utility type must not be null or empty.");
        }

        UtilityType utilityType;
        try {

            utilityType = UtilityType.valueOf(utilityTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {

            return CustomResponseEntity.error("Invalid utility type provided.");
        }


        Account account = accountRepository.findByAccountNumber(consumerNumber);
        if (account == null) {
            return CustomResponseEntity.error("Account not found for consumer number: " + consumerNumber);
        }

        Biller biller = account.getBiller();
        if (biller == null) {
            return CustomResponseEntity.error("Biller not found for account with consumer number: " + consumerNumber);
        }


        if (!biller.getServiceCode().equals(serviceCode)) {
            return CustomResponseEntity.error("Service code does not match the biller’s service code.");
        }


        if (!biller.getUtilityType().equals(utilityType)) {
            return CustomResponseEntity.error("Utility type does not match the biller’s utility type.");
        }

        Bill bill;
        try {
            bill = billRepository.findBillsByAccountAndBillerServiceCodeAndBillerUtilityType(account, serviceCode, utilityType);
        } catch (Exception e) {
            return CustomResponseEntity.error("An error occurred while retrieving bills.");
        }

        if (bill==null) {
            return CustomResponseEntity.error("No bills found for the given parameters.");
        }

        // Prepare the response
        BillDtoResponse response = new BillDtoResponse();
        response.setAccountNumber(consumerNumber);
        response.setCustomerName(account.getCustomer().getName());
        response.setBillerName(biller.getName());
        BillDto billDto = convertToBillDto(bill);
        response.setBill(billDto);



        return new CustomResponseEntity(response,"bill details");
    }

    @Override
    public CustomResponseEntity createBill(BillDto billDto) {

        Bill bill = new Bill();

        Double amount = billDto.getAmount();
        bill.setAmount(amount);

        Double amountForAfterDueDate = Util.calculateAmountForAfterDueDate(amount);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String dueDate = billDto.getDueDate();
        try {
            date =formatter.parse(dueDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        bill.setDueDate(date);
        bill.setAfterDueDateAmount(amountForAfterDueDate);
        bill.setStatus(billDto.getStatus());
        bill.setReferenceNumber(Util.generateBillReference());
        Account account = new Account();
        account.setId(billDto.getAccountId());
        bill.setAccount(account);
        Bill savedBill = billRepository.save(bill);
        return new CustomResponseEntity(savedBill,"bill saved");
    }

    @Override
    public CustomResponseEntity getAllBillers(UtilityType utilityType) {

        Optional<Biller> billerList = billerRepository.findByUtilityType(utilityType);

        if(billerList.isEmpty()){
            return CustomResponseEntity.error("No billers found for this utility type!");
        }
        return new CustomResponseEntity(billerList, "Biller List");
    }

    @Override
    public CustomResponseEntity getAllUtilityTypes() {

        List<UtilityTypeDto> utilityTypeDtos = Arrays.stream(UtilityType.values())
                .map(type -> new UtilityTypeDto(type.name(), type.getIconUrl()))
                .toList();
        return new CustomResponseEntity(utilityTypeDtos,"Utility Types!");
    }

    private static BillDto convertToBillDto(Bill bill) {

        BillDto dto = new BillDto();
        dto.setAmount(Double.valueOf(bill.getAmount()));
        Date dueDate = bill.getDueDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDueDate = formatter.format(dueDate);
        dto.setAmountDueAfterDueDate(bill.getAfterDueDateAmount());
        dto.setDueDate(formattedDueDate);
        dto.setStatus(bill.getStatus());
        dto.setReferenceNumber(bill.getReferenceNumber());
        dto.setAccountId(bill.getAccount().getId());
        return dto;
    }
}
