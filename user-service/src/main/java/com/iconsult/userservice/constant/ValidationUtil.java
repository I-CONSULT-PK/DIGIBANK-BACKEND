package com.iconsult.userservice.constant;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Regular expression for validating an email address
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
//    private static final String NUMBER_REGEX = "^[0-9]{10}$";
    private static final String NUMBER_REGEX = "^92\\d{10}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidNumber(String mobileNumber) {
        return NUMBER_PATTERN.matcher(mobileNumber).matches();
    }

}
