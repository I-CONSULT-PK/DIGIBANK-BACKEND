package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.dto.response.ChequebookDto;
import com.iconsult.userservice.model.entity.Account;
import com.iconsult.userservice.model.entity.Cheque;
import com.iconsult.userservice.model.entity.Chequebook;
import com.iconsult.userservice.model.entity.DigiBankBranch;
import com.iconsult.userservice.repository.*;
import com.iconsult.userservice.service.ChequeService;
import com.iconsult.userservice.service.ChequebookService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ChequebookServiceImpl implements ChequebookService {

    Logger logger = LoggerFactory.getLogger(ChequebookServiceImpl.class);

    @Autowired
    private ChequebookRepository chequebookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ChequeService chequeService;

    @Autowired
    private ChequeRepository chequeRepository;

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    Random random = new Random();


    @Override
    public CustomResponseEntity<ChequebookDto> createChequebookRequest(ChequebookDto chequebookDto, String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return CustomResponseEntity.error("Account is not found!");
        }

        if (account.getDigiBranch() == null) {
            return CustomResponseEntity.error("Branch is not associated with the account!");
        }

        if (chequebookRepository.findByAccountAndStatus(account, "Requested").isPresent()) {
            return CustomResponseEntity.error("Chequebook request already exists for this account.");
        }

        Chequebook chequebook = modelMapper.map(chequebookDto, Chequebook.class);
        chequebook.setBranch(account.getDigiBranch());
        chequebook.setBranch(account.getDigiBranch());
        chequebook.setAccount(account);
        chequebook.setCustomer(account.getCustomer());
        chequebook.setRequestDate(LocalDate.now());
        chequebook.setStatus("Requested");
        chequebook.setCheckType("Personal");

        // Validate cheque pages
        if (chequebook.getChequePages() != 15 && chequebook.getChequePages() != 25) {
            return CustomResponseEntity.error("Cheque Pages must be either 15 or 25");
        }

        List<Cheque> cheques = IntStream.range(0, chequebook.getChequePages())
                .mapToObj(i -> {
                    Cheque cheque = new Cheque();
                    cheque.setChequeNumber(generateRandomChequeNumber());
                    cheque.setChequebook(chequebook);
                    cheque.setIssueDate(LocalDate.now());
                    cheque.setStatus("Issued");
                    return cheque;
                }).collect(Collectors.toList());

        chequebook.setCheques(cheques);

        try {
            Chequebook savedChequebook = chequebookRepository.save(chequebook);
            return new CustomResponseEntity<>(modelMapper.map(savedChequebook, ChequebookDto.class), "Success");
        } catch (Exception e) {
            e.printStackTrace();
            return CustomResponseEntity.error("Error request chequebook: " + e.getMessage());
        }
    }
    private String generateRandomChequeNumber() {
        Long number = 10000000000000L + random.nextLong(90000000000000L);
        // Format the number to ensure it's 14 digits long with leading zeros
        return String.format("%014d", number);
    }

    @Override
    public CustomResponseEntity<String> cancelChequebookRequest(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return CustomResponseEntity.error("Account not found!");
        }

        Optional<Chequebook> chequebookRequest = chequebookRepository.findByAccountAndStatus(account, "Requested");

        if (!chequebookRequest.isPresent()) {
            return CustomResponseEntity.error("No active chequebook request found to cancel.");
        }
        if (!chequebookRequest.get().getStatus().equals("Requested")) {
            return CustomResponseEntity.error("The chequebook request cannot be canceled as it is already processed.");
        }
        chequebookRequest.get().setStatus("Cancelled");
        chequebookRequest.get().setCancelledDate(LocalDate.now());
        chequebookRequest.get().getCheques().forEach(cheque -> cheque.setStatus("Cancelled"));
        try {
            chequebookRepository.save(chequebookRequest.get());
            return new CustomResponseEntity<>("Chequebook request has been successfully canceled.");
        } catch (Exception e) {
            return CustomResponseEntity.error("Error cancelling chequebook request: " + e.getMessage());
        }
    }

    @Override
    public CustomResponseEntity<ChequebookDto> getChequebookById(Long id) {
        Optional<Chequebook> chequebook = chequebookRepository.findById(id);
        if (chequebook.isEmpty()) {
            return CustomResponseEntity.error("Chequebook not found");
        }
        return new CustomResponseEntity<ChequebookDto>(modelMapper.map(chequebook, ChequebookDto.class), "Success");
    }


    @Override
    public CustomResponseEntity<List<ChequebookDto>> getAllChequebooks() {
        List<ChequebookDto> chequebookList = chequebookRepository.findAll().stream().map(chequebook -> modelMapper.map(chequebook, ChequebookDto.class)).collect(Collectors.toList());

        if (chequebookList.isEmpty()) {
            return CustomResponseEntity.error("Chequebooks not available");
        }
        return new CustomResponseEntity<List<ChequebookDto>>(chequebookList, "Success");
    }


    @Override
    public CustomResponseEntity<ChequebookDto> updateChequebookRequest(ChequebookDto chequebookDto, String accountNumber){
        // Find the account by account number
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account==null) {
            return CustomResponseEntity.error("Account not found!");
        }

        // Check if the chequebook exists by ID or some identifier
        Optional<Chequebook> existingChequebook = chequebookRepository.findById(chequebookDto.getId());
        if (existingChequebook.isEmpty()) {
            return CustomResponseEntity.error("Chequebook not found!");
        }

        // Update the existing chequebook's fields
        Chequebook chequebook = existingChequebook.get();
        chequebook.setChequePages(chequebookDto.getChequePages());

        // Ensure the branch is still valid and belongs to the account
        chequebook.setBranch(account.getDigiBranch());
        chequebook.setAccount(account);
        chequebook.setCustomer(account.getCustomer());

        // Update request date and status
        chequebook.setRequestDate(LocalDate.now());
        chequebook.setStatus("Requested");

        // Validate cheque pages and update or generate cheques accordingly
        if (chequebook.getChequePages() != 15 && chequebook.getChequePages() != 25) {
            return CustomResponseEntity.error("Cheque Pages must be either 15 or 25");
        }

        // If cheque pages were updated, regenerate cheques
        List<Cheque> updatedCheques = IntStream.range(0, chequebook.getChequePages())
                .mapToObj(i -> {
                    Cheque cheque = new Cheque();
                    cheque.setChequeNumber(generateRandomChequeNumber());
                    cheque.setChequebook(chequebook);
                    cheque.setIssueDate(LocalDate.now());
                    cheque.setStatus("Unissued");
                    return cheque;
                }).collect(Collectors.toList());

        chequebook.setCheques(updatedCheques);

        // Save the updated chequebook
        Chequebook updatedChequebook = chequebookRepository.save(chequebook);

        // Return the updated response
        return new CustomResponseEntity<>(modelMapper.map(updatedChequebook, ChequebookDto.class), "Update Success");
    }

    @Override
    public CustomResponseEntity<ChequebookDto> deleteChequebook(Long id) {
        Optional<Chequebook> chequebookOptional = chequebookRepository.findById(id);
        if (chequebookOptional.isEmpty()) {
            String errorMessage = String.format("Chequebook with ID %d not found", id);
            return CustomResponseEntity.error(errorMessage);
        }
        chequebookRepository.deleteById(id);
        return new CustomResponseEntity<>(modelMapper.map(chequebookOptional.get(), ChequebookDto.class), "Chequebook deleted successfully");
    }

}
