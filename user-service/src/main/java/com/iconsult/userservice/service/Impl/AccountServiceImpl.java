
package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.custome.Regex;
import com.iconsult.userservice.model.dto.request.AccountDto;
import com.iconsult.userservice.model.dto.response.CbsAccountDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.AccountCDDetails;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.repository.AccountCDDetailsRepository;
import com.iconsult.userservice.repository.AccountRepository;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.service.AccountService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
    //Get Account from Cbs
    private final String getAccountUri = "http://localhost:8081/account/getAccount";

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    Regex regex;
    @Autowired
    AccountCDDetailsRepository accountCDDetailsRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;
//    @Autowired(required = true)
//    AccountCDDetails accountCDDetails;

    @Override
    public Account getAccountsByCustomerCnic(String cnic) {
        return null;
    }

    @Override
    public Account createAccount(AccountDto accountDto) {
        return null;
    }

    public CustomResponseEntity fetchAccountFromCbs(Long customerId, String accountNumber) {
        try {
            // Build the URL with query parameters
            final String bankName = "DigiBank";
            URI uri = UriComponentsBuilder.fromHttpUrl(getAccountUri)
                    .queryParam("accountNumber", accountNumber)
                    .queryParam("bankName", bankName)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri.toString());
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the HTTP GET request
            ResponseEntity<CbsAccountDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CbsAccountDto.class
            );
            return new CustomResponseEntity(response.getBody(), "Success");
        } catch (Exception exception) {
            return CustomResponseEntity.error("Cbs Account not found!");
        }
    }

    public CustomResponseEntity getAccount(Long customerId, String accountNumber) {
        CustomResponseEntity accountFormat = regex.checkAccountNumberFormat(accountNumber);
        if (!accountFormat.isSuccess()){
            return accountFormat;
        }
        CustomResponseEntity<CbsAccountDto> response = fetchAccountFromCbs(customerId, accountNumber);
        Optional<Customer> customer = customerRepository.findById(customerId);
        LOGGER.info("Customer is invalid with id:"+customer);
        if (customer.isPresent() && response.getData() != null) {
            if (customer.get().getCnic().equals(response.getData().getCnicNo())) {
                return new CustomResponseEntity(response.getData(), "Success");
            }
            return CustomResponseEntity.error("You can only link your Accounts");
        }
        // Find the customer by ID
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        // Check if the customer exists
        if (!customerOpt.isPresent()) {
            return CustomResponseEntity.error("Customer id is unknown!!");
        }

        return response;
    }

    @Override
    public CustomResponseEntity addAccount(CbsAccountDto cbsAccountDto) {
        CustomResponseEntity accountFormat = regex.checkAccountNumberFormat(cbsAccountDto.getAccountNumber());
        if (!accountFormat.isSuccess()){
            return accountFormat;
        }
        // Check if the account already exists by account number
        Optional<Account> existingAccount = Optional.ofNullable(accountRepository.findByAccountNumber(cbsAccountDto.getAccountNumber()));
        if(!existingAccount.get().getCustomer().getId().equals(cbsAccountDto.getCustomer().getId())){
            return CustomResponseEntity.error("Invalid Customer Id");
        }
        if (existingAccount.isPresent()) {
            return CustomResponseEntity.error("Account is already exists with account number: " + cbsAccountDto.getAccountNumber());
        }

        // Fetch the account by account number from the repository
        CustomResponseEntity<CbsAccountDto> fetchAccountFromCbs = (CustomResponseEntity<CbsAccountDto>) getAccount(cbsAccountDto.getCustomer().getId(), cbsAccountDto.getAccountNumber());
        if (!fetchAccountFromCbs.isSuccess()){
            return CustomResponseEntity.error(fetchAccountFromCbs.getMessage());
        }
        //CbsAccountDto fetchAccountFromCbs = (CbsAccountDto) getAccount(cbsAccountDto.getCustomer().getId(), cbsAccountDto.getAccountNumber()).getData();

        if (Objects.isNull(fetchAccountFromCbs)) {
            return CustomResponseEntity.error("Account is not available in CBS");
        }

        Account account = new Account();

        // Fetch the customer by customer ID from the repository
        Optional<Customer> customer = customerRepository.findById(cbsAccountDto.getCustomer().getId());

        if (customer.isEmpty() || !customer.get().getId().equals(cbsAccountDto.getCustomer().getId())) {
            return CustomResponseEntity.error("Customer is wrong!");
        }

//        if (cbsAccountDto.getCnicNo().equals(fetchAccountFromCbs.getData().getCnicNo())
//                && cbsAccountDto.getAccountNumber().equals(cbsAccountDto.getAccountNumber())) {
            account.setAccountNumber(fetchAccountFromCbs.getData().getAccountNumber());
            account.setCustomer(cbsAccountDto.getCustomer());
            account.setAccountBalance(fetchAccountFromCbs.getData().getAccountBalance());
            account.setAccountType(fetchAccountFromCbs.getData().getAccountType());
            account.setAccountDescription(fetchAccountFromCbs.getData().getAccountDescription());
            account.setAccountOpenDate(fetchAccountFromCbs.getData().getAccountOpenDate());
            account.setAccountClosedDate(fetchAccountFromCbs.getData().getAccountClosedDate());
            account.setAccountClosedReason(fetchAccountFromCbs.getData().getAccountClosedReason());
            account.setAccountStatus(fetchAccountFromCbs.getData().getAccountStatus());
            account.setIbanCode(fetchAccountFromCbs.getData().getIbanCode());
            account.setDefaultAccount(false);


            Account save = accountRepository.save(account);
            AccountCDDetails accountCDDetails = new AccountCDDetails();
            accountCDDetails.setAccount(save);
            accountCDDetails.setCredit(fetchAccountFromCbs.getData().getLastCredit());
            accountCDDetails.setDebit(fetchAccountFromCbs.getData().getLastDebit());
            accountCDDetails.setPreviousBalance(fetchAccountFromCbs.getData().getAccountBalance());
            accountCDDetails.setActualBalance(fetchAccountFromCbs.getData().getAccountBalance());
            accountCDDetailsRepository.save(accountCDDetails);

            return new CustomResponseEntity<>(save, "Success");
//        return CustomResponseEntity.error("Invalid Customer Id");
    }
}
