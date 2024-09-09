package com.iconsult.userservice.service.Impl;

import java.security.SecureRandom;
import java.util.Random;

public class RandomNumberGeneratorImp {
    public String generateUniqueNumber(int length) {
        if (length <= 0 || length > 18) {
            throw new IllegalArgumentException("Length must be between 1 and 18");
        }

        SecureRandom random = new SecureRandom();
        long min = (long) Math.pow(10, length - 1);
        long max = (long) Math.pow(10, length) - 1;
        return String.valueOf(random.nextLong(max - min + 1) + min);
    }
}
