package com.solution.atmmanagement.model;

import javax.persistence.*;


@Entity
public class UserAccountDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private String recipientName;

    private Double oweAmount;

}
