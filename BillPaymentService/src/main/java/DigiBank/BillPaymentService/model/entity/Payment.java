package DigiBank.BillPaymentService.model.entity;

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
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String amount;
    private Date paymentDate;
    private String transactionId;
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

}
