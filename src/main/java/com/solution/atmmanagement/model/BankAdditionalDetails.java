package com.solution.atmmanagement.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class BankAdditionalDetails extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double creditLimit = 0.0;

    private Double withdrawalLimit = 0.0;

    private Integer pin;


    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    User user;
}
