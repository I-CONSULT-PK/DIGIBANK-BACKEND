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
public class ElectricityPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ElectricityBillConsumer consumer;
    @ManyToOne
    private ElectricityAccount account;
    @ManyToOne
    private ElectricityBill bill;
    private Double amount;
    private Date paymentDate;
    private String status; // e.g., 'successful', 'failed'


}
