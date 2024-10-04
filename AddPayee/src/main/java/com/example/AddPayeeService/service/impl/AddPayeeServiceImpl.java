package com.example.AddPayeeService.service.impl;

import com.example.AddPayeeService.Util.EncrpytionUtil;
import com.example.AddPayeeService.model.dto.BanksDto;
import com.example.AddPayeeService.model.dto.CbsAccountDto;
import com.example.AddPayeeService.model.dto.request.AddPayeeRequestDto;
import com.example.AddPayeeService.model.dto.response.AddPayeeResponseDto;
import com.example.AddPayeeService.model.dto.response.FetchAccountDto;
import com.example.AddPayeeService.model.entity.AddPayee;
import com.example.AddPayeeService.model.mapper.AddPayeeMapper;
import com.example.AddPayeeService.repository.AddPayeeRepository;
import com.example.AddPayeeService.service.AddPayeeService;

import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.apache.tomcat.util.net.openssl.ciphers.Encryption;
import org.bouncycastle.openssl.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.*;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AddPayeeServiceImpl implements AddPayeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddPayeeServiceImpl.class);
    private final String GetAccountTitle = "http://localhost:8084/api/v1/1link/getAccount";
    private final String GetAccountTitle2 = "http://localhost:8080/account/getAccount";
    private final String getLocalAccountTitleURL = "http://localhost:8081/transaction/fetchAccountTitle";

    private final String GetAllBank = "http://localhost:8081/bank";

    private final String GetBankBranchCode = "http://localhost:8081/";

    private final String userActivityURL = "http://localhost:8088/v1/userActivity/saveUserActivity";

    @Autowired
    private AddPayeeRepository addPayeeRepository;

    @Autowired
    private AddPayeeMapper addPayeeMapper;
    @Autowired
    private RestTemplate restTemplate;

    private CustomResponseEntity response;


    @Override
    public CustomResponseEntity createBeneficiary(AddPayeeRequestDto addPayeeRequestDto) throws Exception {
        URI uri = UriComponentsBuilder.fromHttpUrl(userActivityURL)
                .queryParam("userId", addPayeeRequestDto.getCustomerId())
                .queryParam("activity", addPayeeRequestDto.getBeneficiaryAlias()+" has been created")
                .queryParam("pkr",0.0)
                .build()
                .toUri();
        if (addPayeeRequestDto == null) {

            return CustomResponseEntity.error("Beneficiary Data Cannot be null");
        }
        String checkaccountNumber = EncrpytionUtil.decrypt(addPayeeRequestDto.getAccountNumber());
        String decrypt1 = EncrpytionUtil.decrypt(addPayeeRequestDto.getOwnAccount());
        if(checkaccountNumber.equals(decrypt1)){
            return CustomResponseEntity.error("Beneficiary cannot be added to Same Account");

        }
        List<AddPayee> payees = addPayeeRepository.findAllByCustomerId((long) addPayeeRequestDto.getCustomerId());
        for (AddPayee payee : payees) {
            String decryptedAccountNumber = EncrpytionUtil.decrypt(payee.getAccountNumber());
            String decrypt = EncrpytionUtil.decrypt(addPayeeRequestDto.getAccountNumber());

            if (decryptedAccountNumber.equals(decrypt) && payee.getStatus().equals("00")) {
                return CustomResponseEntity.error("This Account already exists");
            } else if (decryptedAccountNumber.equals(addPayeeRequestDto.getAccountNumber()) && payee.getStatus().equals("11")) {
             AddPayee addPayeeExist = addPayeeRepository.findByAccountNumberAndCustomerId(payee.getAccountNumber(),payee.getCustomerId());
                addPayeeExist.setAccountNumber(addPayeeRequestDto.getAccountNumber());
                addPayeeExist.setBeneficiaryName(addPayeeRequestDto.getBeneficiaryName());
                addPayeeExist.setAccountType(addPayeeRequestDto.getAccountType());
                addPayeeExist.setStatus("00");
                addPayeeExist.setBeneficiaryBankName(addPayeeRequestDto.getBeneficiaryBankName());
                addPayeeExist.setFlag(addPayeeRequestDto.getFlag());
                addPayeeExist.setBankCode(addPayeeRequestDto.getBankCode());

//                String encryptedAccountNumber = EncrpytionUtil.encrypt(payee.getAccountNumber());
                addPayeeExist.setAccountNumber(decryptedAccountNumber);
                addPayeeRepository.save(payee);

                Map<String, Object> data = new HashMap<>();
                response = new CustomResponseEntity<>(addPayeeMapper.jpeToDto(payee), "Beneficiary added successfully");
                return response;
            }
        }
//        AddPayee addPayee = addPayeeMapper.dtoToJpe(addPayeeRequestDto);
        AddPayee addPayee = new AddPayee();
        addPayee.setBeneficiaryAlias(addPayeeRequestDto.getBeneficiaryAlias());
        addPayee.setMobileNumber(addPayeeRequestDto.getMobileNumber());
        addPayee.setCategoryType(addPayeeRequestDto.getCategoryType());
        addPayee.setCustomerId(addPayeeRequestDto.getCustomerId());
        addPayee.setBankUrl(addPayeeRequestDto.getBankUrl());
        addPayee.setBeneficiaryBankName(addPayeeRequestDto.getBeneficiaryBankName());
        addPayee.setAccountNumber(addPayeeRequestDto.getAccountNumber());
        addPayee.setBeneficiaryName(addPayeeRequestDto.getBeneficiaryName());
        addPayee.setBankCode(addPayeeRequestDto.getBankCode());
        addPayee.setFlag(false);

        addPayee.setStatus("00");

//        String encryptedAccountNumber = EncrpytionUtil.encrypt(addPayee.getAccountNumber());
        addPayee.setAccountNumber(addPayee.getAccountNumber());
        addPayeeRepository.save(addPayee);

        Map<String, Object> data = new HashMap<>();
        data.put("addPayee" , addPayee);

        response = new CustomResponseEntity<>(data.get("addPayee"), "Beneficiary added successfully");
        return response;
    }

    @Override
    public CustomResponseEntity getBeneficiary(AddPayeeRequestDto addPayeeRequestDto) throws Exception {
        return null;
    }

    @Override
    public CustomResponseEntity getAccountDetails(String accountNumber, String bankName) {

        try {

            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(GetAccountTitle)
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

            // Make HTTP GET request
            ResponseEntity<CbsAccountDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CbsAccountDto.class
            );

            // Handle response
            //  if (response.getStatusCode() == HttpStatus.OK) { // 200 status code
            CbsAccountDto fetchAccount = response.getBody();
            if (fetchAccount != null) {
                // Print or log responseDto to verify its content
                LOGGER.info("Received Account Number: " + fetchAccount.toString());

                // Create new AccountDTO and map fields
                AddPayeeResponseDto addPayeeResponse = new AddPayeeResponseDto();
                addPayeeResponse.setAccountNumber(fetchAccount.getAccountNumber());
                addPayeeResponse.setAccountType(fetchAccount.getAccountType());
                addPayeeResponse.setBeneficiaryName(fetchAccount.getAccountTitle());
//                addPayeeResponse.setBeneficiaryBankName(fetchAccount.getBankName());
                return new CustomResponseEntity<CbsAccountDto>(fetchAccount,"Banks reterived successfully");
            }
//            else {
//                // No customer found
//                return null;
//            }
//            } else {
//                // Handle error response or non-200 status
//                LOGGER.error("Unexpected response status: " + response.getStatusCode());
//                return null;
//            }

        } catch (Exception e) {

        }

        try {

            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(GetAccountTitle2)
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

            // Make HTTP GET request
            ResponseEntity<CbsAccountDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CbsAccountDto.class
            );

            // Handle response
            //  if (response.getStatusCode() == HttpStatus.OK) { // 200 status code
            CbsAccountDto fetchAccount = response.getBody();
            if (fetchAccount != null) {
                // Print or log responseDto to verify its content
                LOGGER.info("Received Account Number: " + fetchAccount.toString());

                // Create new AccountDTO and map fields
                AddPayeeResponseDto addPayeeResponse = new AddPayeeResponseDto();
                addPayeeResponse.setAccountNumber(fetchAccount.getAccountNumber());
                addPayeeResponse.setAccountType(fetchAccount.getAccountType());
                addPayeeResponse.setBeneficiaryName(fetchAccount.getAccountTitle());
                return new CustomResponseEntity<CbsAccountDto>(fetchAccount,"Banks reterived successfully");
            }
            else {
                // No customer found
                return null;
            }
//            } else {
//                // Handle error response or non-200 status
//                LOGGER.error("Unexpected response status: " + response.getStatusCode());
//                return null;
//            }

        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
            return new CustomResponseEntity<>(404, "Account does not exist");
        }
//        return  this.addPayeeRepository.save(add);
    }

    public CustomResponseEntity<List<BanksDto>> getAllBanks() {

        ResponseEntity<List<BanksDto>> response = restTemplate.exchange(
                GetAllBank,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BanksDto>>() {
                }
        );

        return new CustomResponseEntity<List<BanksDto>>(response.getBody(),"Banks reterived successfully");
    }

    @Override
    public CustomResponseEntity deleteBene(Long beneId) {
        AddPayee addPayee = this.addPayeeRepository.findById(beneId).orElseThrow(() -> new RuntimeException("Beneficiary is not found with given Id:" + beneId));
        addPayee.setStatus("11");
        addPayeeRepository.save(addPayee);
        return new CustomResponseEntity<>("Beneficiary deleted successfully");


    }

    @Override
    public CustomResponseEntity updateBene(AddPayeeRequestDto addPayeeRequestDto) {
        try {
            // Fetch the existing AddPayee entity from the repository
            AddPayee addPayee = this.addPayeeRepository
                    .findByCustomerIdAndId(Long.valueOf(addPayeeRequestDto.getCustomerId()),addPayeeRequestDto.getBeneId()
                            )
                    .orElseThrow(() -> new RuntimeException("Beneficiary not found with given CustomerId: "
                            + addPayeeRequestDto.getCustomerId()));

            // Update the entity with new values from the DTO
//            CbsAccountDto cbsAccountDto = getAccountDetails(addPayeeRequestDto.getAccountNumber());
            addPayee.setBeneficiaryAlias(addPayeeRequestDto.getBeneficiaryAlias());
            addPayee.setMobileNumber(addPayeeRequestDto.getMobileNumber());
            // Save the updated entity back to the repository
            addPayeeRepository.save(addPayee);

            // Return a success response with the updated beneficiary data
            return new CustomResponseEntity<>(addPayeeMapper.jpeToDto(addPayee), "Beneficiary updated successfully");

        } catch (RuntimeException e) {
            // Handle exceptions such as beneficiary not found or any other runtime issues
            return new CustomResponseEntity<>(null, "Error updating beneficiary: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unforeseen exceptions
            return new CustomResponseEntity<>(null, "An unexpected error occurred: " + e.getMessage());
        }
    }


//    public List<BankBranchesDetailDto> getBankBranchCode() {
//
//        ResponseEntity<List<BankBranchesDetailDto>> response = restTemplate.exchange(
//                GetAllBank,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<BankBranchesDetailDto>>() {}
//        );
//
//        return response.getBody();
//    }


    @Override
    public CustomResponseEntity getAllBeneficiaries(Long customerId , Boolean flag) throws Exception {

        List<CustomResponseEntity<AddPayeeResponseDto>> responses = new ArrayList<>();
        try {
            if(flag == true){
                List<AddPayee> addPayees = this.addPayeeRepository.findAllByCustomerIdAndStatusAndFlag(customerId,"00",true).orElseThrow(() -> new RuntimeException("Beneficiary not found with given CustomerId: "
                        +customerId ));
                response = new CustomResponseEntity<>(addPayeeMapper.jpeToDtoList(addPayees), "Beneficiary retrieved successfully");
            } else if (flag == false) {
                List<AddPayee> addPayees = this.addPayeeRepository.findAllByCustomerIdAndStatus(customerId,"00").orElseThrow(() -> new RuntimeException("Beneficiary not found with given CustomerId: "
                        +customerId ));
                response = new CustomResponseEntity<>(addPayeeMapper.jpeToDtoList(addPayees), "Beneficiary retrieved successfully");
            }
    } catch (Exception e) {
            responses.add(new CustomResponseEntity<>(null, "Error retrieving beneficiaries" + e.getMessage()));

        }
        return response;
    }

    @Override
    public CustomResponseEntity getAddPayee(Long beneId) {
        AddPayee addPayee = this.addPayeeRepository.findById(beneId).orElseThrow(() -> new RuntimeException("Beneficiary is not found with given Id:" + beneId));
        response = new CustomResponseEntity<>(addPayeeMapper.jpeToDto(addPayee), "Beneficiary retrieved successfully");
        return response;

    }

    @Override
    public CustomResponseEntity getLocalAccountTitle(String senderAccountNumber) {
        try {
            // Build URL with path variables
            URI uri = UriComponentsBuilder.fromHttpUrl(getLocalAccountTitleURL)
                    .queryParam("accountNumber", senderAccountNumber)
                    .build()
                    .toUri();

            // Log the full request URL
            LOGGER.info("Request URL: " + uri);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make HTTP GET request
            ResponseEntity<CustomResponseEntity> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    CustomResponseEntity.class
            );

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK) { // 200 status code
                CustomResponseEntity<FetchAccountDto> responseDto = response.getBody();
                if (responseDto != null) {
                    // Print or log responseDto to verify its content
                    LOGGER.info("Received FetchAccountDto: " + responseDto.getMessage());
                    if (!responseDto.isSuccess()) return responseDto;

                    return responseDto;
                } else {
                    // No customer found
                    return CustomResponseEntity.error("Unable to Process!");
                }
            } else {
                // Handle error response or non-200 status
                LOGGER.error("Unexpected response status: " + response.getStatusCode());
                return CustomResponseEntity.error("Unable to Process!");
            }

        } catch (Exception e) {
            // Handle exceptions
            LOGGER.error("Exception occurred: ", e);
            return CustomResponseEntity.error("Unable to Process!");
        }
    }

    @Override
    public CustomResponseEntity addToFavourite(Long beneId , boolean flag, Long customerId) {


       try {
           AddPayee addPayee = addPayeeRepository.findByCustomerIdAndId(customerId, beneId).orElse(null);
           if(Objects.isNull(addPayee)){
               return CustomResponseEntity.error("Beneficiary Does not Exist");
           }
           if(addPayee.getFlag() == true){
               addPayee.setFlag(false);
               addPayeeRepository.save(addPayee);
               response = new CustomResponseEntity<>(addPayeeMapper.jpeToDto(addPayee), "Beneficiary removed from favourite  successfully");

           } else if (addPayee.getFlag() == null) {
               addPayee.setFlag(flag);
               addPayeeRepository.save(addPayee);
               response = new CustomResponseEntity<>(addPayeeMapper.jpeToDto(addPayee), "Beneficiary added to favourite  successfully");
           } else {
               addPayee.setFlag(true);
               addPayeeRepository.save(addPayee);
               response = new CustomResponseEntity<>(addPayeeMapper.jpeToDto(addPayee), "Beneficiary added to favourite successfully");

           }

       }
       catch (Exception e){
           LOGGER.error("Exception occurred: ", e);
           return CustomResponseEntity.error("Unable to Process!");
       }
        return response;
    }

    @Override
    public CustomResponseEntity addTransferAmount(String accountNumber, String transferAmount , Long customerId) throws Exception {
        List<AddPayee> payees = addPayeeRepository.findAllByCustomerId((long) customerId);
        if(Objects.isNull(payees)){
            return null;
        }else
        {
            for (AddPayee payee : payees) {
                String decryptedAccountNumber = EncrpytionUtil.decrypt(payee.getAccountNumber());

                if (decryptedAccountNumber.equals(accountNumber) && payee.getStatus().equals("00")) {
                    payee.setLastTransferAmount(transferAmount);
                    addPayeeRepository.save(payee);
                }
            }
        }
        response = new CustomResponseEntity<>(true,"Transfer Amount updated");
        return response;
    }



}
