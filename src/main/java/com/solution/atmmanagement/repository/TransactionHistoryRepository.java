package com.solution.atmmanagement.repository;

import com.solution.atmmanagement.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory,Integer> {
}
