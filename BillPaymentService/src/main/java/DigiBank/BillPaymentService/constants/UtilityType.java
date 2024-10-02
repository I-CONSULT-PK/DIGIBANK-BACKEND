package DigiBank.BillPaymentService.constants;

public enum UtilityType {

    GAS("Gas"),
    WATER("Water"),
    ELECTRICITY("Electricity"),
    TELECOMMUNICATION("Telecommunication"),
    INTERNET("Internet"),
    CREDIT_CARD("Credit Card");

    private final String description;

    UtilityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;

    }
}