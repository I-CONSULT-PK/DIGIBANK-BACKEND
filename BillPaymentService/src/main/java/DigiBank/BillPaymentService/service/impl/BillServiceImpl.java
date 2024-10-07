package DigiBank.BillPaymentService.service.impl;

import DigiBank.BillPaymentService.constants.BillStatus;
import DigiBank.BillPaymentService.constants.CustomResponseEntity;
import DigiBank.BillPaymentService.constants.Util;
import DigiBank.BillPaymentService.constants.UtilityType;
import DigiBank.BillPaymentService.model.dto.UtilityTypeDto;
import DigiBank.BillPaymentService.model.dto.request.BillDto;
import DigiBank.BillPaymentService.model.dto.request.BillDtoResponse;
import DigiBank.BillPaymentService.model.dto.request.BillPaymentDto;
import DigiBank.BillPaymentService.model.dto.request.BillerDtoRequest;
import DigiBank.BillPaymentService.model.entity.Account;
import DigiBank.BillPaymentService.model.entity.Bill;
import DigiBank.BillPaymentService.model.entity.BillPayment;
import DigiBank.BillPaymentService.model.entity.Biller;
import DigiBank.BillPaymentService.model.mapper.BillerMapper;
import DigiBank.BillPaymentService.repository.AccountRepository;
import DigiBank.BillPaymentService.repository.BillRepository;
import DigiBank.BillPaymentService.repository.BillerRepository;
import DigiBank.BillPaymentService.repository.PaymentRepository;
import DigiBank.BillPaymentService.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillerRepository billerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillerMapper billerMapper;


    Date currentDate = new Date();
    @Override
    public CustomResponseEntity addBiller(BillerDtoRequest request) {

        if (request.getUtilityType() == null) {
            return CustomResponseEntity.error( "Utility type must not be null.");
        }

        if (!isValidUtilityType(request.getUtilityType())) {
            return CustomResponseEntity.error ("Invalid utility type: " + request.getUtilityType());
        }

        if(billerRepository.findByContactNumber(request.getContactNumber()).isPresent()){
            return CustomResponseEntity.error("A biller with the this contactNumber " + request.getContactNumber() + " already exists.");
        }
        if (billerRepository.findByName(request.getName()).isPresent()) {
            return CustomResponseEntity.error("A biller with the this name " + request.getName() + " already exists.");
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
    private CustomResponseEntity validateInputs(String consumerNumber, String serviceCode, String utilityTypeString) {
        if (consumerNumber == null || consumerNumber.trim().isEmpty()) {
            return CustomResponseEntity.error("Consumer number must not be null or empty.");
        }
        if (serviceCode == null || serviceCode.trim().isEmpty()) {
            return CustomResponseEntity.error("Service code must not be null or empty.");
        }
        if (utilityTypeString == null || utilityTypeString.trim().isEmpty()) {
            return CustomResponseEntity.error("Utility type must not be null or empty.");
        }
        return null;
    }


    private boolean isValidBiller(Biller biller, String serviceCode, UtilityType utilityType) {
        return biller.getServiceCode().equals(serviceCode) && biller.getUtilityType().equals(utilityType);
    }
    @Override
    public CustomResponseEntity getBillDetailsByConsumerNumber(String consumerNumber, String serviceCode, String utilityTypeString) {

        CustomResponseEntity validationError = validateInputs(consumerNumber, serviceCode, utilityTypeString);

        if (validationError != null) {
            return validationError;
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
        if (!isValidBiller(biller, serviceCode, utilityType)) {
            return CustomResponseEntity.error("Service code or utility type does not match the biller’s details.");
        }

        return handleBillDetails(account, serviceCode, utilityType, consumerNumber, biller);
    }
    private CustomResponseEntity handleBillDetails(Account account, String serviceCode, UtilityType utilityType, String consumerNumber, Biller biller) {
        try {
          //  Bill bill = billRepository.findBillsByAccountAndBillerServiceCodeAndBillerUtilityType(account, serviceCode, utilityType);
            Bill bill = billRepository.findBillsByAccountAndBillerServiceCodeAndBillerUtilityType(account, serviceCode, utilityType);

            if (bill.getStatus() == BillStatus.PAID) {

                Optional<BillPayment> billPayment = bill.getBillPayments().stream()
                        .sorted(Comparator.comparing(BillPayment::getPaymentDate).reversed())
                        .findFirst();


                BillPaymentDto paymentDto = convertToBillPaymentDto(billPayment.get());
                return  new CustomResponseEntity(paymentDto, "Already Paid!");
            }

            if (bill == null) {
                return CustomResponseEntity.error("Bill not found or already paid!");
            }



            // Prepare the response for unpaid bill
            BillDtoResponse response = new BillDtoResponse();
            response.setAccountNumber(consumerNumber);
            response.setCustomerName(account.getCustomer().getName());
            response.setBillerName(biller.getName());

            if (bill.getDueDate().before(currentDate)) {
                response.setValidAmount(bill.getAfterDueDateAmount());
            } else {
                response.setValidAmount(bill.getAmount());
            }
            BillDto billDto = convertToBillDto(bill);
            billDto.setId(bill.getId());
            response.setBill(billDto);

            return new CustomResponseEntity(response, "bill details");

        } catch (Exception e) {
            return CustomResponseEntity.error("An error occurred while retrieving bills.");
        }
    }

    private BillPaymentDto  convertToBillPaymentDto(BillPayment billPayment ) {

        BillPaymentDto dto = new BillPaymentDto();
        dto.setId(billPayment.getId());
        dto.setPaymentDate(billPayment.getPaymentDate());
        dto.setBillId(billPayment.getId());
        dto.setStatus(billPayment.getStatus());
        dto.setTransactionId(billPayment.getTransactionId());
        dto.setAmount(billPayment.getAmount());
        return dto;
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
    public CustomResponseEntity getAllBillers(String utilityTypeParam) {

        UtilityType utilityType;
        try {
            utilityType = UtilityType.valueOf(utilityTypeParam.toUpperCase());
        }catch (IllegalArgumentException ex) {
            return CustomResponseEntity.error("Invalid Request Param for utility type!");
        }

        List<Biller> billerList = billerRepository.findByUtilityType(utilityType);

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

    @Override
    public CustomResponseEntity payBill(Long billId) {

        Optional<Bill> billOptional = billRepository.findById(billId);

        if(billOptional.isEmpty()){
            return CustomResponseEntity.error("Bill not found with this id" +billId);
        }


        Bill bill = billOptional.get();

        if (bill.getStatus() == BillStatus.PAID) {
            return CustomResponseEntity.error("Already Paid");
        }

        bill.setStatus(BillStatus.PAID);

        BillPayment billPayment = new BillPayment();
        billPayment.setPaymentDate(new Date());
        billPayment.setStatus(BillStatus.PAID);
        billPayment.setBill(bill);

        if (bill.getDueDate().before(currentDate)) {
            billPayment.setAmount(bill.getAfterDueDateAmount());
        } else {
            billPayment.setAmount(bill.getAmount());
        }
        billPayment.setTransactionId(Util.generateTransactionId());
        paymentRepository.save(billPayment);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy hh:mm a");

        String formattedDate = dateFormat.format(billPayment.getPaymentDate());

//        billRepository.delete(billOptional.get());

        Map<String, Object> map = new HashMap<>();
        map.put("Amount Paid!", billPayment.getAmount());
        map.put("transactionId", billPayment.getTransactionId());
        map.put("transactionDate",formattedDate);
        return new CustomResponseEntity( map, "Bill paid successfully!");
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
