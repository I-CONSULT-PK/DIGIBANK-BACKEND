package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.FundTransferDto;
import com.iconsult.userservice.model.dto.request.InterBankFundTransferDto;
import com.iconsult.userservice.model.dto.request.ScheduleFundTransferDto;
import com.iconsult.userservice.model.dto.request.ScheduleIbftFundTransferDto;
import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.quartz.SchedulerException;

import java.util.Map;

public interface FundTransferService {

    CustomResponseEntity getAllBanks();

    CustomResponseEntity getAccountTitle(String senderAccountNumber);

    CustomResponseEntity fundTransfer(FundTransferDto cbsTransferDto);

    CustomResponseEntity interBankFundTransfer(InterBankFundTransferDto interBankFundTransferDto);

    CustomResponseEntity<Map<String, Object>>getTransactionsByAccountAndDateRange(
            String accountNumber, String startDate, String endDate);

    CustomResponseEntity<Map<String, Object>> generateMiniStatement(String accountNumber);

    CustomResponseEntity<Map<String, Object>> generateStatement(String accountNumber, String startDate, String endDate, String statementType);

    CustomResponseEntity setOneDayLimit(String account ,Long customerId, Double ondDayLimit);

    CustomResponseEntity scheduleFundTransfer(ScheduleFundTransferDto fundTransferDto) throws SchedulerException;

    CustomResponseEntity scheduleIbftFundTransfer(ScheduleIbftFundTransferDto fundTransferDto);
}

