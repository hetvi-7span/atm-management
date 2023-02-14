package com.solution.atmmanagement.service.impl;

import com.solution.atmmanagement.exception.AtmTransactionException;
import com.solution.atmmanagement.model.*;
import com.solution.atmmanagement.repository.BankAdditionalRepository;
import com.solution.atmmanagement.repository.BankRepository;
import com.solution.atmmanagement.repository.TransactionHistoryRepository;
import com.solution.atmmanagement.repository.UserRepository;
import com.solution.atmmanagement.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Service
public class AtmServiceImpl implements AtmService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    BankAdditionalRepository bankAdditionalRepository;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;


    @Transactional
    @Override
    public String login(String username) {
        String msg = "";
        User existingUser = userRepository.findByUsername(username);
        if(existingUser == null){
            User newUser;
            try {
                User user = new User();
                user.setUsername(username);
                newUser = userRepository.save(user);

                BankDetails bankDetails = new BankDetails();
                bankDetails.setUser(newUser);
                bankDetails.setBankAdditionalDetails(new BankAdditionalDetails());
                bankRepository.save(bankDetails);

                createHistory(newUser,this.messageSource.getMessage("user.created.history", new Object[]{}, LocaleContextHolder.getLocale()),TransactionType.CREATE);

                msg = this.messageSource.getMessage("user.created", new Object[]{newUser.getUsername(),bankDetails.getBalance()}, LocaleContextHolder.getLocale());
            }catch (ConstraintViolationException e){
                return this.messageSource.getMessage("user.exist", new Object[]{}, LocaleContextHolder.getLocale());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            msg = this.messageSource.getMessage("user.created", new Object[]{existingUser.getUsername(),existingUser.getBankDetails().getBalance()}, LocaleContextHolder.getLocale());
        }
        return msg;
    }

    @Override
    public String deposit(Double amount){
         BankDetails bankDetails = getBankDetails();
         if(Objects.nonNull(bankDetails)){
                bankDetails.setBalance(bankDetails.getBalance() + amount);
                bankRepository.save(bankDetails);
                createHistory(bankDetails.getUser(),this.messageSource.getMessage("user.deposit.history", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale()),TransactionType.DEPOSIT);
         }
        return this.messageSource.getMessage("user.deposit", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale());
    }

    @Override
    public String withdraw(Double amount){
        BankDetails bankDetails = getBankDetails();
        BankAdditionalDetails bankAdditionalDetails = Objects.nonNull(bankDetails)?bankDetails.getBankAdditionalDetails():null;
        if(bankAdditionalDetails.getWithdrawalLimit() < amount){
              throw new AtmTransactionException( this.messageSource.getMessage("user.withdraw.limit.exceeded", new Object[]{},LocaleContextHolder.getLocale()));
        }
        if(Objects.nonNull(bankDetails) && bankDetails.getBalance() > amount){
            bankDetails.setBalance(bankDetails.getBalance() - amount);
            bankRepository.save(bankDetails);
        }
        createHistory(bankDetails.getUser(),this.messageSource.getMessage("user.withdraw.history", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale()),TransactionType.WITHDRAW);

        return this.messageSource.getMessage("user.withdraw", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale());
    }

    @Override
    public String setWithdrawLimit(Double amount) {
        BankDetails bankDetails = getBankDetails();
        BankAdditionalDetails bankAdditionalDetails = bankDetails.getBankAdditionalDetails();
        if(amount == 0.0){
            bankDetails.setIsWithdrawalLimitGenerated(Boolean.FALSE);
            bankRepository.save(bankDetails);
        }else{
            bankDetails.setIsWithdrawalLimitGenerated(Boolean.TRUE);
            bankAdditionalDetails.setWithdrawalLimit(amount);
            bankRepository.save(bankDetails);
            bankAdditionalRepository.save(bankAdditionalDetails);
        }
        if(Objects.nonNull(bankDetails.getUser())) {
            createHistory(bankDetails.getUser(), this.messageSource.getMessage("user.withdraw.limit", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale()), TransactionType.WITHDRAW);
        }
        return this.messageSource.getMessage("user.withdraw.limit", new Object[]{bankAdditionalDetails.getWithdrawalLimit()}, LocaleContextHolder.getLocale());
    }


    @Override
    public String setCreditLimit(Double amount) {
        BankDetails bankDetails = getBankDetails();
        BankAdditionalDetails bankAdditionalDetails = bankDetails.getBankAdditionalDetails();
        if(amount == 0.0){
            bankDetails.setIsCreditLimitGenerated(Boolean.FALSE);
            bankRepository.save(bankDetails);
        }else{
            bankDetails.setIsCreditLimitGenerated(Boolean.TRUE);
            bankAdditionalDetails.setCreditLimit(amount);
            bankRepository.save(bankDetails);
            bankAdditionalRepository.save(bankAdditionalDetails);
        }
        if(Objects.nonNull(bankDetails.getUser())) {
            createHistory(bankDetails.getUser(), this.messageSource.getMessage("user.credit.limit", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale()), TransactionType.DEPOSIT);
        }
        return this.messageSource.getMessage("user.credit.limit", new Object[]{bankAdditionalDetails.getCreditLimit()}, LocaleContextHolder.getLocale());
    }

    private BankDetails getBankDetails() {
        return bankRepository.findByUserId(1);
    }

    private void createHistory(User newUser,String msg, TransactionType type) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionType(type);
        transactionHistory.setUser(newUser);
        transactionHistory.setDescription(msg);
        transactionHistoryRepository.save(transactionHistory);
    }

}
