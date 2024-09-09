package com.iconsult.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "Complaint")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Complaint implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "complaint_seq")
    @SequenceGenerator(name = "complaint_seq", sequenceName = "complaint_sequence", allocationSize = 1)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerId")
    @JsonBackReference
    private Customer customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ComplaintType")
    @JsonBackReference
    private ComplaintsLogs complaintType;
    private String description;
    @Column(name = "open_date")
    private String complainOpenDate;
    private String complainNumber;
}
