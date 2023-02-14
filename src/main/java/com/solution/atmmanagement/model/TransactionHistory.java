package com.solution.atmmanagement.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class TransactionHistory extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    private Double amount;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

}
