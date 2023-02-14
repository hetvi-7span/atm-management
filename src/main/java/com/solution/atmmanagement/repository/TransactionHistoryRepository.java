package com.solution.atmmanagement.repository;

import com.solution.atmmanagement.model.TransactionHistory;
import com.solution.atmmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory,Integer> {

    List<TransactionHistory> findTop5ByUserOrderByCreateDateDesc(User user);
}
