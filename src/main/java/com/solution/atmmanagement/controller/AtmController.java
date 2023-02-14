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
     * This command used for login
     * Create user if not exits
     * @param username from CLI
     * @return Hello, {username}! Your balance is $ {balance}
     */
    @ShellMethod("login user !!!")
    public String login(String username){
        return atmService.login(username);
    }

    /**
     * This command used for deposit amount
     * @param amount data
     * @return Your balance is $ {balance}
     */
    @ShellMethod
    public String deposit(Double amount){
        return atmService.deposit(amount);
    }


    /**
     * This command used for withdraw amount
     * @param amount data
     * @return Your balance is $ {balance}
     */
    @ShellMethod
    public String withdraw(Double amount){
        return atmService.withdraw(amount);
    }

 /*   @ShellMethod
    public String limit(String type,Double amount){
        if(type != null && type.equalsIgnoreCase("Withdraw")){
            return atmService.setWithdrawLimit(amount);
        }else{
            return atmService.setCreditLimit(amount);
        }
    }*/


    /**
     * This command used for transfer amount
     * @param username,amount data
     * @return Transferred ${amount} to {username} . Your balance is ${amount}.
     * @return Transferred ${amount} to {username}. Your balance is ${amount}.Owed ${amount} to {username}.
     */
    @ShellMethod("transfer")
    public String transfer(String username,Double amount){
        return atmService.transferAmount(username,amount);
    }

    @ShellMethod("logout")
    public String logout(){
        return atmService.logout();
    }

    /**
     * This command used to get current balance in account
     * @return {username} your current balance is ${amount}.
     */
    @ShellMethod("balance")
    public String balance(){
        return atmService.getCurrentBalance();
    }

    @ShellMethod
    public String statement(){
        return atmService.statement();
    }

}
