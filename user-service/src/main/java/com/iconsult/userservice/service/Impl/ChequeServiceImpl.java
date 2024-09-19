package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.dto.response.ChequeDto;
import com.iconsult.userservice.model.entity.Cheque;
import com.iconsult.userservice.repository.ChequeRepository;
import com.iconsult.userservice.service.ChequeService;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChequeServiceImpl implements ChequeService {

    @Autowired
    private ChequeRepository chequeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CustomResponseEntity<ChequeDto> createCheque(ChequeDto chequeDto) {
        Cheque cheque = modelMapper.map(chequeDto, Cheque.class);
        Cheque savedCheque = chequeRepository.save(cheque);
        ChequeDto savedChequeDto = modelMapper.map(savedCheque, ChequeDto.class);
        return new CustomResponseEntity<>(savedChequeDto, "Cheque created successfully.");
    }

    @Override
    public CustomResponseEntity<ChequeDto> cancelCheque(ChequeDto chequeDto, String chequeNumber) {

        Optional<Cheque> existingChequeOptional = chequeRepository.findByChequeNumber(chequeNumber);
        if (existingChequeOptional.isEmpty()) {
            return CustomResponseEntity.error("Cheque not found!");
        }
        Cheque cheque = existingChequeOptional.get();
        if (!cheque.getStatus().equalsIgnoreCase("Issued")) {
            return CustomResponseEntity.error("The cheque cannot be canceled as it has already been issued or processed.");
        }
        // Update cheque status to 'Cancelled'
        cheque.setStatus("Cancelled");
        cheque.setCancelledDate(LocalDate.now());
        cheque.setChequeNumber(existingChequeOptional.get().getChequeNumber());
        cheque.setChequebook(existingChequeOptional.get().getChequebook());
        cheque.setId(existingChequeOptional.get().getId());
        cheque.setIssueDate(existingChequeOptional.get().getIssueDate());
        try {
            Cheque updatedCheque = chequeRepository.save(cheque);
            ChequeDto updatedChequeDto = modelMapper.map(updatedCheque, ChequeDto.class);
            return new CustomResponseEntity<>(updatedChequeDto, "Cheque status has been successfully canceled.");
        } catch (Exception e) {
            return CustomResponseEntity.error("Error canceling cheque status: " + e.getMessage());
        }
    }

    @Override
    public CustomResponseEntity<ChequeDto> getCheque(String chequeNumber) {
        Optional<Cheque> cheque = chequeRepository.findByChequeNumber(chequeNumber);
        return cheque.map(value -> new CustomResponseEntity<>(modelMapper.map(value, ChequeDto.class), "Success")).orElseGet(() -> CustomResponseEntity.error("Cheque not found!"));
    }

    @Override
    public CustomResponseEntity<List<ChequeDto>> getAllCheques() {
        List<ChequeDto> chequeList = chequeRepository.findAll()
                .stream()
                .filter(cheque -> "Issued".equalsIgnoreCase(cheque.getStatus())) // Filter by status
                .map(cheque -> modelMapper.map(cheque, ChequeDto.class))
                .collect(Collectors.toList());

        if (chequeList.isEmpty()) {
            return CustomResponseEntity.error("No issued cheques available");
        }
        return new CustomResponseEntity<>(chequeList, "Success");
    }

    @Override
    public CustomResponseEntity<ChequeDto> updateCheque(String chequeNumber, ChequeDto chequeDto) {
        Optional<Cheque> optionalCheque = chequeRepository.findByChequeNumber(chequeNumber);

        if (optionalCheque.isEmpty()) {
            return CustomResponseEntity.error("Cheque not found");
        }

        Cheque existingCheque = optionalCheque.get();
        existingCheque.setStatus(chequeDto.getStatus());

        Cheque updatedCheque = chequeRepository.save(existingCheque);
        ChequeDto updatedChequeDto = modelMapper.map(updatedCheque, ChequeDto.class);
        return new CustomResponseEntity<>(updatedChequeDto, "Cheque status updated successfully");
    }


    @Override
    public CustomResponseEntity<ChequeDto> deleteCheque(String chequeNumber) {
        Optional<Cheque> cheque = chequeRepository.findByChequeNumber(chequeNumber);
        if (cheque.isEmpty()) {
            return CustomResponseEntity.error("Cheque not found");
        }
        chequeRepository.deleteById(cheque.get().getId());
        return new CustomResponseEntity<>(null, "Cheque deleted successfully");
    }

}
