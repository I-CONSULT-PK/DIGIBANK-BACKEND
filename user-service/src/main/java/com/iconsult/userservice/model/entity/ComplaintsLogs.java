package com.iconsult.userservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ComplaintLogs")
public class ComplaintsLogs implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;
    @Id
    private String complaintType;
    private int receive;
    private int closed;

    @Override
    public String toString() {
        return "ComplaintsLogs{" +
                "complaintType='" + complaintType + '\'' +
                ", receive=" + receive +
                ", closed=" + closed +
                '}';
    }
}
