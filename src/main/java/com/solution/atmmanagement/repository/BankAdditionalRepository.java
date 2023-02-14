package com.solution.atmmanagement.repository;

import com.solution.atmmanagement.model.BankAdditionalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAdditionalRepository extends JpaRepository<BankAdditionalDetails,Integer>
{
}
