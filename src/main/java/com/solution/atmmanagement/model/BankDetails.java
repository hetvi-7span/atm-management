package com.solution.atmmanagement.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter
@Getter
@ToString
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

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    BankAdditionalDetails bankAdditionalDetails;

}
