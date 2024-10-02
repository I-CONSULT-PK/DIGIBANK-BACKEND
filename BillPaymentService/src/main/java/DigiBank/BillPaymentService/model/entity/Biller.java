package DigiBank.BillPaymentService.model.entity;

import DigiBank.BillPaymentService.constants.UtilityType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Biller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String serviceCode;
    @Enumerated(EnumType.STRING)
    private UtilityType utilityType;
    private String contactNumber;
    private String address;
    private String iconUrl;
    @OneToMany(mappedBy = "biller")
    @JsonIgnore
    private Set<Account> accounts;
}
