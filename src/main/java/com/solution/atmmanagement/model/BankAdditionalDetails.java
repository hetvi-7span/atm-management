package com.solution.atmmanagement.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
public class BankAdditionalDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double creditLimit = 0.0;

    private Double withdrawalLimit = 0.0;

    private Integer pin;

    @CreationTimestamp
    @Column(name = "created_date")
    private Date createDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Date updatedDate;

}
