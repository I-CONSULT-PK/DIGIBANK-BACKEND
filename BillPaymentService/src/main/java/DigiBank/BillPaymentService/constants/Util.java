package DigiBank.BillPaymentService.constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Util {
    public static String generateUniqueServiceCode(String prefix) {
        prefix = prefix.replace(" ", "");

        // Get the current timestamp formatted as 'yyyyMMddHHmmss'
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//        String timestamp = LocalDateTime.now().format(formatter);

        // Generate a random UUID and take the first 8 characters
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Combine prefix, timestamp, and random part to form the service code
        String serviceCode = prefix + randomPart;

        return serviceCode;
    }

    public static String generateTransactionId() {
        // Create a Random object
        Random random = new Random();

        // Generate a random 12-digit number (from 100000000000 to 999999999999)
        long transactionId = 100000000000L + (long)(random.nextDouble() * 900000000000L);

        // Return the transaction ID as a string
        return String.valueOf(transactionId);
    }

    public static String generateBillReference() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());

        Random random = new Random();
        int randomNum = 10000000 + random.nextInt(90000000);

        return  currentDate + randomNum;
    }

    public static Double calculateAmountForAfterDueDate(Double amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        double increasePercentage = 0.05; // 5% increase
        return amount * (1 + increasePercentage);
    }
}
