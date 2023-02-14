package com.solution.atmmanagement.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "user_details",uniqueConstraints = { @UniqueConstraint(columnNames = { "user_name" }) })
@Getter
@Setter
@ToString
public class User extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name="mobile_no")
    private String mobileNumber;

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_detail_id",referencedColumnName = "id")*/
    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER)
    BankDetails bankDetails;




}
