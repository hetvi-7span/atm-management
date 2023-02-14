package com.solution.atmmanagement.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "user_details",uniqueConstraints = { @UniqueConstraint(columnNames = { "user_name" }) })
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name="mobile_no")
    private String mobileNumber;

    @CreationTimestamp
    @Column(name = "created_date")
    private Date createDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Date updatedDate;

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_detail_id",referencedColumnName = "id")*/
    @OneToOne(mappedBy = "user")
    BankDetails bankDetails;
}
