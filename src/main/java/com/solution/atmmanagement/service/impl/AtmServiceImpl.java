package com.solution.atmmanagement.service.impl;

import com.solution.atmmanagement.dto.CurrentUserDto;
import com.solution.atmmanagement.model.*;
import com.solution.atmmanagement.repository.*;
import com.solution.atmmanagement.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    UserOwingRepository userOwingRepository;

    CurrentUserDto currentUserDto;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;


    @Transactional
    @Override
    public String login(String username) {

        String msg = null;
        if (currentUserDto != null) {
            msg = this.messageSource.getMessage("logout.before.login", new Object[]{currentUserDto.getUsername()}, LocaleContextHolder.getLocale());
            return msg;
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            User newUser;
            try {
                User user = new User();
                user.setUsername(username);
                newUser = userRepository.save(user);

                BankDetails bankDetails = new BankDetails();
                bankDetails.setUser(newUser);
                bankDetails.setBankAdditionalDetails(new BankAdditionalDetails());
                bankRepository.save(bankDetails);

                //createHistory(newUser, this.messageSource.getMessage("user.created.history", new Object[]{}, LocaleContextHolder.getLocale()), TransactionType.CREATE);

                msg = this.messageSource.getMessage("user.created", new Object[]{newUser.getUsername(), bankDetails.getBalance()}, LocaleContextHolder.getLocale());
                currentUserDto = currentUser(newUser.getUsername(), 0.0, newUser.getId());
            } catch (ConstraintViolationException e) {
                return this.messageSource.getMessage("user.exist", new Object[]{}, LocaleContextHolder.getLocale());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            List<UserOwingDetails> userOwingDetails = userOwingRepository.findByDebtorId(existingUser.getId());

            StringBuilder owingMsg = new StringBuilder();
            if (!userOwingDetails.isEmpty()) {
                for (UserOwingDetails userOwingDetail : userOwingDetails) {
                    Optional<User> creditor = userRepository.findById(userOwingDetail.getCreditorId());
                    creditor.ifPresent(user -> owingMsg.append("Owed $").append(userOwingDetail.getOwingAmount()).append(" to ").append(user.getUsername()).append(".\n"));
                }
            }

            msg = this.messageSource.getMessage("existing.user", new Object[]{existingUser.getUsername(), existingUser.getBankDetails().getBalance(), owingMsg}, LocaleContextHolder.getLocale());
            currentUserDto = currentUser(existingUser.getUsername(), existingUser.getBankDetails().getBalance(), existingUser.getId());
        }
        return msg;


    }

    @Override
    public String transferAmount(String username, Double amount) {
        String msg;

        if (currentUserDto == null) {
            msg = this.messageSource.getMessage("please.login", new Object[]{}, LocaleContextHolder.getLocale());
            return msg;
        }


        User targetUser = userRepository.findByUsername(username);
        if (targetUser == null) {
            msg = this.messageSource.getMessage("user.not.exist", new Object[]{username}, LocaleContextHolder.getLocale());
            return msg;
        }


        User sourceUser = userRepository.findByUsername(currentUserDto.getUsername());
        Double currentUserBalance = sourceUser.getBankDetails().getBalance();

        if (amount > currentUserBalance) {

            List<UserOwingDetails> existingOwingDetails = userOwingRepository.findByDebtorId(sourceUser.getId());

            List<UserOwingDetails> isTargetUserAsCreditor = existingOwingDetails.stream().filter(userOwingDetails -> userOwingDetails.getCreditorId().equals(targetUser.getId())).collect(Collectors.toList());

            UserOwingDetails userOwingDetails = null;
            double owingAmount = amount - currentUserBalance;

            if (!isTargetUserAsCreditor.isEmpty()) {
                userOwingDetails = isTargetUserAsCreditor.get(0);
                owingAmount = owingAmount + isTargetUserAsCreditor.get(0).getOwingAmount();

            } else {
                userOwingDetails = new UserOwingDetails();
                userOwingDetails.setDebtorId(sourceUser.getId());
                userOwingDetails.setCreditorId(targetUser.getId());
            }


            userOwingDetails.setOwingAmount(owingAmount);

            userOwingRepository.save(userOwingDetails);


            BankDetails sourceUserBankDetails = getUserBankDetails(currentUserDto.getUserId());
            sourceUserBankDetails.setBalance(0.0);

            BankDetails targetUserBankDetails = getUserBankDetails(targetUser.getId());
            targetUserBankDetails.setBalance(targetUserBankDetails.getBalance() + currentUserBalance);

            BankDetails save = bankRepository.save(sourceUserBankDetails);
            bankRepository.save(targetUserBankDetails);

            msg = this.messageSource.getMessage("amount.transfer.owning.balance", new Object[]{currentUserBalance, targetUser.getUsername(), owingAmount}, LocaleContextHolder.getLocale());

            createHistory(sourceUser, this.messageSource.getMessage("user.transfer.history", new Object[]{amount,username,save.getBalance()}, LocaleContextHolder.getLocale()), TransactionType.TRANSFER);
        } else {

            BankDetails sourceUserBankDetails = getUserBankDetails(currentUserDto.getUserId());
            sourceUserBankDetails.setBalance(currentUserBalance - amount);

            BankDetails targetUserBankDetails = getUserBankDetails(targetUser.getId());
            targetUserBankDetails.setBalance(targetUserBankDetails.getBalance() + amount);

            BankDetails sourceUserWithUpdatedDetails = bankRepository.save(sourceUserBankDetails);
            bankRepository.save(targetUserBankDetails);

            msg = this.messageSource.getMessage("amount.transfer.balance", new Object[]{amount, username, sourceUserWithUpdatedDetails.getBalance()}, LocaleContextHolder.getLocale());

            String historyMessage = this.messageSource.getMessage("user.transfer.history", new Object[]{amount,username,sourceUserWithUpdatedDetails.getBalance()}, LocaleContextHolder.getLocale());

            createHistory(sourceUser, historyMessage, TransactionType.TRANSFER);
        }

        return msg;
    }

    @Override
    public String logout() {
        String msg;

        if (currentUserDto == null) {
            msg = this.messageSource.getMessage("please.login", new Object[]{}, LocaleContextHolder.getLocale());
            return msg;
        }

        String user = currentUserDto.getUsername();
        currentUserDto = null;
        msg = this.messageSource.getMessage("user.logout", new Object[]{user}, LocaleContextHolder.getLocale());
        return msg;
    }

    @Override
    public String deposit(Double amount) {

        String msg;

        if (currentUserDto == null) {
            msg = this.messageSource.getMessage("please.login", new Object[]{}, LocaleContextHolder.getLocale());
            return msg;
        }

        BankDetails bankDetails = getUserBankDetails(currentUserDto.getUserId());
        if (Objects.nonNull(bankDetails)) {
            bankDetails.setBalance(bankDetails.getBalance() + amount);
            bankRepository.save(bankDetails);
            createHistory(bankDetails.getUser(), this.messageSource.getMessage("user.deposit.history", new Object[]{amount,bankDetails.getBalance()}, LocaleContextHolder.getLocale()), TransactionType.DEPOSIT);
        }
        msg = this.messageSource.getMessage("user.deposit", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale());
        return msg;
    }

    @Override
    public String withdraw(Double amount) {

        String msg;

        if (currentUserDto == null) {
            msg = this.messageSource.getMessage("please.login", new Object[]{}, LocaleContextHolder.getLocale());
            return msg;
        }

        BankDetails bankDetails = getUserBankDetails(currentUserDto.getUserId());
        BankAdditionalDetails bankAdditionalDetails = Objects.nonNull(bankDetails) ? bankDetails.getBankAdditionalDetails() : null;
       /* if(bankAdditionalDetails.getWithdrawalLimit() < amount){
              throw new AtmTransactionException( this.messageSource.getMessage("user.withdraw.limit.exceeded", new Object[]{},LocaleContextHolder.getLocale()));
        }*/
        if (Objects.nonNull(bankDetails) && bankDetails.getBalance() > amount) {
            bankDetails.setBalance(bankDetails.getBalance() - amount);
            bankRepository.save(bankDetails);
        }
        createHistory(bankDetails.getUser(), this.messageSource.getMessage("user.withdraw.history", new Object[]{amount,bankDetails.getBalance()}, LocaleContextHolder.getLocale()), TransactionType.WITHDRAW);

        msg = this.messageSource.getMessage("user.withdraw", new Object[]{bankDetails.getBalance()}, LocaleContextHolder.getLocale());
        return msg;
    }

    @Override
    public String getCurrentBalance() {
        String msg;

        if (currentUserDto == null) {
            msg = this.messageSource.getMessage("please.login", new Object[]{}, LocaleContextHolder.getLocale());
            return msg;
        }

        msg = this.messageSource.getMessage("current.balance", new Object[]{currentUserDto.getUsername(),currentUserDto.getCurrentBalance()}, LocaleContextHolder.getLocale());
        return msg;
    }

    @Override
    public String statement() {

        User user = userRepository.findByUsername(currentUserDto.getUsername());

        List<TransactionHistory> transactionHistories =  transactionHistoryRepository.findTop5ByUserOrderByCreateDateDesc(user);

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0 ; i < transactionHistories.size() ; i++) {
            stringBuilder.append(i+1).append(") " ).append(transactionHistories.get(i).getCreateDate()).append(" || Transaction type : ")
                    .append(transactionHistories.get(i).getTransactionType())
                    .append(" || Description : ")
                    .append(transactionHistories.get(i).getDescription()).append("\n");
        }

        return stringBuilder.toString();
    }

    private CurrentUserDto currentUser(String username, Double balance, Integer userId) {
        return CurrentUserDto.builder().username(username).currentBalance(balance).userId(userId).build();
    }
/*    @Override
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
    }*/

    /*@Override
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
    }*/

    private BankDetails getUserBankDetails(Integer userId) {
        return bankRepository.findByUserId(userId);
    }

    private void createHistory(User newUser, String msg, TransactionType type) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionType(type);
        transactionHistory.setUser(newUser);
        transactionHistory.setDescription(msg);
        transactionHistoryRepository.save(transactionHistory);
    }

}
