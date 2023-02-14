package com.solution.atmmanagement.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class BankDetails extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double balance = 0.0;

    private Boolean isCreditLimitGenerated = Boolean.FALSE;
    private Boolean isWithdrawalLimitGenerated = Boolean.FALSE;

    private Boolean isPinGenerated = Boolean.FALSE;

    @OneToOne(cascade = CascadeType.ALL)
    User user;
}
