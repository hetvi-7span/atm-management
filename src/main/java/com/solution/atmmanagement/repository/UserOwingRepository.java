package com.solution.atmmanagement.repository;

import com.solution.atmmanagement.model.UserOwingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOwingRepository extends JpaRepository<UserOwingDetails,Integer> {
    List<UserOwingDetails> findByDebtorId(Integer userId);
    List<UserOwingDetails> findByDebtorIdOrderByOwingAmountDesc(Integer userId);
    List<UserOwingDetails> findByCreditorId(Integer userId);
}
