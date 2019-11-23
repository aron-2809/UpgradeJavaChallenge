package com.upgrade.upgradejavachallenge.exceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String errMsg) {
        super(errMsg);
    }

    public InvalidInputException(String errMsg, RuntimeException e) {
        super(errMsg, e);
    }
}
