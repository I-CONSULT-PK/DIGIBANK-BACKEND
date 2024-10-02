package DigiBank.BillPaymentService.model.dto.request;


import DigiBank.BillPaymentService.constants.UtilityType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BillerDtoRequest {

    private String contactNumber;
    private String address;
    private String name;
    private UtilityType utilityType;
    private String iconUrl;


}
