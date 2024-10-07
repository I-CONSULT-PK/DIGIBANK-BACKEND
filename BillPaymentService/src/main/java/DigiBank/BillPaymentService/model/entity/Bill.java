package DigiBank.BillPaymentService.model.entity;

import DigiBank.BillPaymentService.constants.BillStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private Date dueDate;
    private Double afterDueDateAmount;
    @Enumerated(EnumType.STRING)
    private BillStatus status;
    private String referenceNumber;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @OneToMany(mappedBy = "bill")
    private Set<BillPayment> billPayments;

}
