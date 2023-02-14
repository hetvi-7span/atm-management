package com.solution.atmmanagement.service;

public interface AtmService {
    String login(String username);

    String transferAmount(String username, Double amount);

    String logout();

}
