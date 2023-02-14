package com.solution.atmmanagement.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class BankDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Double balance;

    private Boolean isCreditLimitGenerated;
    private Boolean isWithdrawalLimitGenerated;

    private Boolean isPinGenerated;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    User user;

    @CreationTimestamp
    @Column(name = "created_date")
    private Date createDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Date updatedDate;

}
