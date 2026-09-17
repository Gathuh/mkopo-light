package com.tezzar.mkopo.light.exception;

public class InvalidLoanStateException extends RuntimeException {
    public InvalidLoanStateException(String from, String to) {
        super("Cannot transition loan from " + from + " to " + to);
    }

    public InvalidLoanStateException(String message) {
        super(message);
    }
}
