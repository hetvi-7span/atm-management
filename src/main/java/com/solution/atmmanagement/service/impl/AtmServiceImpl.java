package com.solution.atmmanagement.service.impl;

import com.solution.atmmanagement.model.BankAdditionalDetails;
import com.solution.atmmanagement.model.BankDetails;
import com.solution.atmmanagement.model.User;
import com.solution.atmmanagement.repository.BankAdditionalRepository;
import com.solution.atmmanagement.repository.BankRepository;
import com.solution.atmmanagement.repository.UserRepository;
import com.solution.atmmanagement.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

@Service
@Transactional
public class AtmServiceImpl implements AtmService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    BankAdditionalRepository bankAdditionalRepository;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;


    @Override
    public String login(String username) {
        String msg;
        User existingUser = userRepository.findByUsername(username);
        if(existingUser == null){
            User newUser;
            try {
                User user = new User();
                user.setUsername(username);
                newUser = userRepository.save(user);

                BankDetails bankDetails = new BankDetails();
                bankDetails.setUser(newUser);
                bankRepository.save(bankDetails);

                BankAdditionalDetails bankAdditionalDetails = new BankAdditionalDetails();
                bankAdditionalDetails.setUser(newUser);
                bankAdditionalRepository.save(bankAdditionalDetails);

                msg = this.messageSource.getMessage("user.created", new Object[]{newUser.getUsername(),bankDetails.getBalance()}, LocaleContextHolder.getLocale());
                return msg;

            }catch (ConstraintViolationException e){
                return this.messageSource.getMessage("user.exist", new Object[]{}, LocaleContextHolder.getLocale());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            msg = this.messageSource.getMessage("user.created", new Object[]{existingUser.getUsername(),existingUser.getBankDetails().getBalance()}, LocaleContextHolder.getLocale());
            return msg;
        }

        //uuid,id,username

        return "";
    }
}
