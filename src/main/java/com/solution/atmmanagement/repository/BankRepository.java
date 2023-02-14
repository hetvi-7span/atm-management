package com.solution.atmmanagement.repository;

import com.solution.atmmanagement.model.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<BankDetails,Integer> {
}
