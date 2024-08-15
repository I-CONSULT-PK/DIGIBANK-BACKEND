package com.example.AddPayeeService.service.impl;

import com.example.AddPayeeService.Util.EncrpytionUtil;
import com.example.AddPayeeService.model.dto.BanksDto;
import com.example.AddPayeeService.model.dto.CbsAccountDto;
import com.example.AddPayeeService.model.dto.request.AddPayeeRequestDto;
import com.example.AddPayeeService.model.dto.response.AddPayeeResponseDto;
import com.example.AddPayeeService.model.entity.AddPayee;
import com.example.AddPayeeService.model.mapper.AddPayeeMapper;
import com.example.AddPayeeService.repository.AddPayeeRepository;
import com.example.AddPayeeService.service.AddPayeeService;

import com.zanbeel.customUtility.model.CustomResponseEntity;
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
import java.util.*;

@Service
public class AddPayeeServiceImpl implements AddPayeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddPayeeServiceImpl.class);
    private final String GetAccountTitle = "http://192.168.0.153:8081/account/getAccount";
    private final String GetAccountTitle2 = "http://192.168.0.138:8080/account/getAccount";

    private final String GetAllBank = "http://localhost:8081/bank";

    private final String GetBankBranchCode = "http://localhost:8081/";

    @Autowired
    private AddPayeeRepository addPayeeRepository;

    @Autowired
    private AddPayeeMapper addPayeeMapper;
    @Autowired
    private RestTemplate restTemplate;

    private CustomResponseEntity response;


    @Override
    public CustomResponseEntity createBeneficiary(AddPayeeRequestDto addPayeeRequestDto) throws Exception {

        if (addPayeeRequestDto == null) {

            return CustomResponseEntity.error("Beneficiary Data Cannot be null");
        }
        EncrpytionUtil encrpytionUtil = new EncrpytionUtil();
        List<AddPayee> payees = addPayeeRepository.findAllByCustomerId((long) addPayeeRequestDto.getCustomerId());
        for (AddPayee payee : payees) {
            String decryptedAccountNumber = encrpytionUtil.decrypt(payee.getAccountNumber(), "your_secure_key");
            if (decryptedAccountNumber.equals(addPayeeRequestDto.getAccountNumber())) {
                return CustomResponseEntity.error("This Account already exists");
            }
        }
//        AddPayee accountNumberExists = addPayeeRepository.findByAccountNumberAndCustomerId(addPayeeRequestDto.getAccountNumber(), addPayeeRequestDto.getCustomerId());
//        if(accountNumberExists != null){
//
//            return CustomResponseEntity.error("This Account is already exist");
//        }

//        CbsAccountDto cbsAccountDto = getAccountDetails(addPayeeRequestDto.getAccountNumber());
        AddPayee addPayee = addPayeeMapper.dtoToJpe(addPayeeRequestDto);
        addPayee.setAccountNumber(addPayeeRequestDto.getAccountNumber());
        addPayee.setBeneficiaryName(addPayeeRequestDto.getBeneficiaryName());
        addPayee.setAccountType(addPayeeRequestDto.getAccountType());
        addPayee.setStatus("00");

        String encryptedAccountNumber = encrpytionUtil.encrypt("your_secure_key", addPayee.getAccountNumber());
        addPayee.setAccountNumber(encryptedAccountNumber);
        addPayeeRepository.save(addPayee);

        Map<String, Object> data = new HashMap<>();
        response = new CustomResponseEntity<>(addPayeeMapper.jpeToDto(addPayee), "Beneficiary added successfully");
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
            addPayee.setAccountNumber(addPayeeRequestDto.getAccountNumber());
            addPayee.setStatus("00");
            addPayee.setAccountType(addPayeeRequestDto.getAccountType());
            addPayee.setBeneficiaryName(addPayeeRequestDto.getBeneficiaryName());
            addPayee.setBeneficiaryAlias(addPayeeRequestDto.getBeneficiaryAlias());
            addPayee.setBeneficiaryEmailId(addPayeeRequestDto.getBeneficiaryEmailId());
            addPayee.setCategoryId(addPayeeRequestDto.getCategoryId());
            addPayee.setCustomerId(addPayeeRequestDto.getCustomerId());
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
    public List<CustomResponseEntity<AddPayeeResponseDto>> getAllBeneficiaries(Long customerId) throws Exception {

        List<CustomResponseEntity<AddPayeeResponseDto>> responses = new ArrayList<>();
        try {
            List<AddPayee> addPayees = this.addPayeeRepository.findAllByCustomerIdAndStatus(customerId,"00").orElseThrow(() -> new RuntimeException("Beneficiary not found with given CustomerId: "
                    +customerId ));
            EncrpytionUtil encrpytionUtil = new EncrpytionUtil();
            List<AddPayeeResponseDto> addPayeeResponseDtos = addPayeeMapper.jpeToDtoList(addPayees);
            for (AddPayeeResponseDto addPayee : addPayeeResponseDtos) {
                try {
                    String decryptedAccountNumber = encrpytionUtil.decrypt(addPayee.getAccountNumber(), "your_secure_key");
                    addPayee.setAccountNumber(decryptedAccountNumber);
                    CustomResponseEntity<AddPayeeResponseDto> response = new CustomResponseEntity<>(addPayee, "Beneficiary retrieved successfully");
                    responses.add(response);
                } catch (EncryptionException e) {
                    responses.add(new CustomResponseEntity<>(null, "Error decrypting account number"));
                }
            }
    } catch (Exception e) {
            responses.add(new CustomResponseEntity<>(null, "Error retrieving beneficiaries" + e.getMessage()));

        }
        return responses;
    }

    @Override
    public CustomResponseEntity getAddPayee(Long beneId) {
        AddPayee addPayee = this.addPayeeRepository.findById(beneId).orElseThrow(() -> new RuntimeException("Beneficiary is not found with given Id:" + beneId));
        response = new CustomResponseEntity<>(addPayeeMapper.jpeToDto(addPayee), "Beneficiary retrieved successfully");
        return response;

    }

}
