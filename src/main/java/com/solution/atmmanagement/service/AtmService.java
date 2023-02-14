package com.solution.atmmanagement.service;

public interface AtmService {
    String login(String username);

    String transferAmount(String username, Double amount);

    String logout();

    String deposit(Double amount);
    String withdraw(Double amount);

    String getCurrentBalance();

    String statement();

   /* String setWithdrawLimit(Double amount);
    String setCreditLimit(Double amount);*/

}
