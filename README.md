
# atm-management

An atm-management is a Command Line Interface (CLI) to simulate an interaction of an ATM with a retail bank.


## Prerequisite
1. Java version 1.8 or higher
2. Mysql db connection require 
3. Idea required to run the project

## Steps to run a project
1. Run AtmManagementApplication.java file 
## Assumptions
1. Considering user is valid and pre-authenticated.
2. User will enter correct commands to run an application error free.
3. User will give appropriate input to justify the command 
4. In the case of a deposit when the user has multiple creditors that case user will pay the amount which has the maximum owing amount. 

## Implemented features
1. login
2. logout
3. deposit money
4. withdrawal money
5. transfer money 
6. Current balance inqury
7. Generate mini statement
## Excecutable Commands
1. 'login Alice' : login or  create user if not exist 
2. 'deposit 100' : deposit amount in current logged in user's account
3. 'transfer Bob 50' : transfer amount in another beneficiary user account
4. 'withdraw 20' : to withdraw amount from account
5. 'checkBalance' : to check your current balance in account
6. 'statement' : to generate the mini-statement of latest 5 transactions with their description and time.
7. logout : to logged out from the system.
## future enhancement
: As this is a real time problem so we can enhance the existing system by providing below functionalities.


1. we can provide pin generation,updation functionality for account holders.
2. Authenticate user's transaction by asking them to enter a pin.
3. we can implement a custom authenticatation using mobile number or email address with otp.
4. we can provide a functionality to set owing and withdrawal limit to limt the transactions. (limit can be per month or per day)
5. we can reset the limits by using schedulars.
