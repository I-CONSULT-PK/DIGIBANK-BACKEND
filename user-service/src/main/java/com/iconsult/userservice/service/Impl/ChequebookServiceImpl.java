package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.dto.response.ChequebookDto;
import com.iconsult.userservice.model.entity.*;
import com.iconsult.userservice.repository.*;
import com.iconsult.userservice.service.ChequeService;
import com.iconsult.userservice.service.ChequebookService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
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

        Chequebook chequebook = modelMapper.typeMap(ChequebookDto.class, Chequebook.class)
                .addMappings(mapper -> {
                    mapper.skip(Chequebook::setCustomer);
                })
                .map(chequebookDto);

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

        // Generate cheques for the chequebook
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

        // Save the chequebook
        try {
            Chequebook savedChequebook = chequebookRepository.save(chequebook);
            return new CustomResponseEntity<>(modelMapper.map(savedChequebook, ChequebookDto.class), "Success");
        } catch (Exception e) {
            e.printStackTrace();
            return CustomResponseEntity.error("Error requesting chequebook: " + e.getMessage());
        }
    }

    private String generateRandomChequeNumber() {
        Long number = 10000000000000L + random.nextLong(90000000000000L);
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
        if (!chequebookRequest.get().getStatus().equalsIgnoreCase("Requested")) {
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


//    @Override
//    public CustomResponseEntity<List<ChequebookDto>> getAllChequebooks() {
//        List<ChequebookDto> chequebookList = chequebookRepository.findAll().stream()
//                .filter(chequebook -> chequebook.getCheques().stream()
//                        .anyMatch(cheque -> cheque.getStatus().equalsIgnoreCase("Issued")))
//                .map(chequebook -> {
//                    ChequebookDto chequebookDto = new ChequebookDto();
//                    chequebookDto.setId(chequebook.getId());
//                    chequebookDto.setCheckType(chequebook.getCheckType());
//                    chequebookDto.setStatus(chequebook.getStatus());
//                    chequebookDto.setRequestDate(chequebook.getRequestDate());
//                    chequebookDto.setChequePages(chequebook.getChequePages());
//
//                    // Populate branchCode and accountNumber from associated entities
//                    DigiBankBranch branch = chequebook.getBranch();
//                    if (branch != null) {
//                        chequebookDto.setBranchCode(branch.getBranchCode());
//                    }
//
//                    Account account = chequebook.getAccount();
//                    if (account != null) {
//                        chequebookDto.setAccountNumber(account.getAccountNumber());
//                    }
//
//                    // Populate customer details
//                    Customer customer = chequebook.getCustomer();
//                    if (customer != null) {
//                        chequebookDto.setFirstName(customer.getFirstName()); // Assuming there's a getFirstName method
//                        chequebookDto.setLastName(customer.getLastName());   // Assuming there's a getLastName method
//                    }
//
//                    // Map cheques to ChequeDto if needed
//                    List<ChequeDto> chequeDtos = chequebook.getCheques().stream()
//                            .map(cheque -> modelMapper.map(cheque, ChequeDto.class))
//                            .collect(Collectors.toList());
//                    chequebookDto.setCheques(chequeDtos);
//
//                    return chequebookDto;
//                })
//                .collect(Collectors.toList());
//
//        if (chequebookList.isEmpty()) {
//            return CustomResponseEntity.error("No chequebooks available with issued cheques");
//        }
//
//        return new CustomResponseEntity<>(chequebookList, "Success");
//    }


    @Override
    public CustomResponseEntity<List<ChequebookDto>> getAllChequebooks() {
        // Fetch all chequebooks and filter based on cheque status being "Issued"
        List<ChequebookDto> chequebookList = chequebookRepository.findAll().stream()
                .filter(chequebook -> chequebook.getCheques().stream()
                        .anyMatch(cheque -> cheque.getStatus().equalsIgnoreCase("Issued")))
                .map(chequebook -> modelMapper.map(chequebook, ChequebookDto.class))
                .collect(Collectors.toList());
        if (chequebookList.isEmpty()) {
            return CustomResponseEntity.error("No chequebooks available with issued cheques");
        }
        return new CustomResponseEntity<List<ChequebookDto>>(chequebookList, "Success");
    }

    @Override
    public CustomResponseEntity<ChequebookDto> updateChequebookRequest(ChequebookDto chequebookDto, Long chequebookId) {

        Optional<Chequebook> existingChequebookOptional = chequebookRepository.findById(chequebookId);
        if (existingChequebookOptional.isEmpty()) {
            return CustomResponseEntity.error("Chequebook not found!");
        }

        Chequebook chequebook = existingChequebookOptional.get();

        if (!chequebook.getStatus().equals("Requested")) {
            return CustomResponseEntity.error("The chequebook request cannot be updated as it is not in a 'Requested' state.");
        }

        chequebook.setChequePages(chequebookDto.getChequePages());
        chequebook.setRequestDate(LocalDate.now());
        chequebook.setStatus("Requested");
        chequebook.setCheckType("Personal");

        if (chequebook.getChequePages() != 15 && chequebook.getChequePages() != 25) {
            return CustomResponseEntity.error("Cheque Pages must be either 15 or 25");
        }

        List<Cheque> existingCheques = chequebook.getCheques();
        List<Cheque> updatedCheques = IntStream.range(0, chequebook.getChequePages())
                .mapToObj(i -> {
                    Cheque cheque;
                    if (i < existingCheques.size()) {
                        // Reuse existing cheques if possible
                        cheque = existingCheques.get(i);
                    } else {
                        // Create new cheques if needed
                        cheque = new Cheque();
                    }
                    cheque.setChequeNumber(generateRandomChequeNumber());
                    cheque.setChequebook(chequebook);
                    cheque.setIssueDate(LocalDate.now());
                    cheque.setStatus("Issued");
                    return cheque;
                }).collect(Collectors.toList());
        // Clear existing cheques and set updated cheques
        chequebook.getCheques().clear();
        chequebook.getCheques().addAll(updatedCheques);
        try {
            Chequebook updatedChequebook = chequebookRepository.save(chequebook);
            return new CustomResponseEntity<>(modelMapper.map(updatedChequebook, ChequebookDto.class), "Update Success");
        } catch (Exception e) {
            return CustomResponseEntity.error("Error updating chequebook request: " + e.getMessage());
        }
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
