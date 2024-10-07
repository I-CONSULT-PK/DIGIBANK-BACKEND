package DigiBank.BillPaymentService.model.dto.request;

import DigiBank.BillPaymentService.constants.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BillPaymentDto {

    private Long id;
    private Double amount;
    private Date paymentDate;
    private String transactionId;
    private BillStatus status;
    private Long billId;
}
