package com.solution.atmmanagement.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
public class BankDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double balance = 0.0;

    private Boolean isCreditLimitGenerated = Boolean.FALSE;
    private Boolean isWithdrawalLimitGenerated = Boolean.FALSE;

    private Boolean isPinGenerated = Boolean.FALSE;

    @OneToOne(cascade = CascadeType.ALL)
    User user;

    @CreationTimestamp
    @Column(name = "created_date")
    private Date createDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Date updatedDate;

}
