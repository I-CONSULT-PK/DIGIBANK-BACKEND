package DigiBank.BillPaymentService.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BillDtoResponse {

    private String accountNumber;

    private String customerName;

    private String billerName;
    private BillDto bill;
}
