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
public class BillDto {

    private Long id;
    private Double amount;
    private String dueDate;
    private Double amountDueAfterDueDate;
    private BillStatus status;
    private String referenceNumber;
    private Long consumerId;
}
