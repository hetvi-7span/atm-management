package com.solution.atmmanagement.controller;

import com.solution.atmmanagement.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class AtmController {

    @Autowired
    AtmService atmService;

    /**
     * This method used for login
     * Create user if not exits
     * @param username from CLI
     * @return Hello, {username}! Your balance is $ {balance}
     */
    @ShellMethod("login user !!!")
    public String login(String username){
        return atmService.login(username);
    }

    @ShellMethod
    public String deposit(Double amount){
        return atmService.deposit(amount);
    }

    @ShellMethod
    public String withdraw(Double amount){
        return atmService.withdraw(amount);
    }

    @ShellMethod
    public String limit(String type,Double amount){
        if(type != null && type.equalsIgnoreCase("Withdraw")){
            return atmService.setWithdrawLimit(amount);
        }else{
            return atmService.setCreditLimit(amount);
        }
    }

}
