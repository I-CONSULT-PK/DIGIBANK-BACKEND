package DigiBank.BillPaymentService.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentResponse {

    private Long paymentId;
    private Double amount;
    private String status;
    private Date paymentDate;

}
