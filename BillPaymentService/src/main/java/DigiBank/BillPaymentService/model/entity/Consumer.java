package DigiBank.BillPaymentService.model.entity;

import DigiBank.BillPaymentService.constants.UtilityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Consumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String consumerNumber;
    @Enumerated(EnumType.STRING)
    private UtilityType utilityType;
//    private Double balance;
    private String billingCycle; // monthly
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "biller_id")
    private Biller biller;
    @OneToMany(mappedBy = "consumer")
    private Set<Bill> bills;

}
