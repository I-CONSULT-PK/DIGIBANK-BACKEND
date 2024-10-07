package DigiBank.BillPaymentService.model.entity;

import DigiBank.BillPaymentService.constants.BillStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double amount;
    private Date paymentDate;
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private BillStatus status;
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

}
