package com.solution.atmmanagement.controller;

import com.solution.atmmanagement.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class AtmController {

    @Autowired
    AtmService atmService;

    @ShellMethod("login user !!!")
    public String login(String username){
        return atmService.login(username);

    }
}
