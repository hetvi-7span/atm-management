package com.solution.atmmanagement.exception;

public class AtmTransactionException extends RuntimeException{
    public AtmTransactionException(String errorMessage) {
        super(errorMessage);
    }
}
