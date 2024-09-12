package com.iconsult.userservice.custome;

import com.zanbeel.customUtility.model.CustomResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class Regex {
    private static final String ACCOUNT_PATTERN = "^zanbeel-\\w+$";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public boolean isValidAccount(String accountNumber) {
        Pattern pattern = Pattern.compile(ACCOUNT_PATTERN);
        Matcher matcher = pattern.matcher(accountNumber);
        return matcher.matches();
    }
    public boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public CustomResponseEntity checkAccountNumberFormat(String accountNumber){
        if (!isValidAccount(accountNumber)){
            return CustomResponseEntity.error("Account must be in the format 'zanbeel-xxxx', where xxxx is alphanumeric.");
        }
        return new CustomResponseEntity<>(accountNumber);
    }
}
