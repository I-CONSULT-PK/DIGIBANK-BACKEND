package com.admin_service.enumeration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public enum TimePeriod {

    ONE_DAY {
        @Override
        public LocalDate calculateStartDate() {
            return LocalDate.now().minus(1, ChronoUnit.DAYS);
        }
    },
    ONE_MONTH {
        @Override
        public LocalDate calculateStartDate() {
            return LocalDate.now().minus(1, ChronoUnit.MONTHS);
        }
    },
    ONE_YEAR {
        @Override
        public LocalDate calculateStartDate() {
            return LocalDate.now().minus(1, ChronoUnit.YEARS);
        }
    };

    // Abstract method that each enum constant must implement
    public abstract LocalDate calculateStartDate();

    // Factory method to parse enum from a string without using switch-case
    public static TimePeriod fromString(String period) {
        // Clean up input and match it to enum constant without switch-case
//        return TimePeriod.valueOf(period.replace(" ", "_").toUpperCase());
        //try {
        // Clean up input and match it to enum constant
        return TimePeriod.valueOf(period.replace(" ", "_").toUpperCase());
//        } catch (IllegalArgumentException e) {
//            // Throw custom error message if invalid period is provided
//            throw new IllegalArgumentException("Invalid input: " + period + ". Valid options are: '1 day', '1 month', or '1 year'.");
//        }
    }
}

