package com.iconsult.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "userActivity")
public class UserActivity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_Activity")
    private String activity;

    @Column(name = "activity_Date")
    private LocalDateTime activityDate;

    @JsonBackReference
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customerId;
    @Column(name = "pkr")
    private Double pkr;

    @Override
    public String toString() {
        return "UserActivity{" +
                "id=" + id +
                ", activity='" + activity + '\'' +
                ", activityDate=" + activityDate +
                ", customerId=" + customerId +
                ", pkr=" + pkr +
                '}';
    }
}
