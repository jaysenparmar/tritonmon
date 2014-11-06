package com.tritonmon.exception;

public class PartyException extends Exception {
    public PartyException() {
        super();
    }

    public PartyException(String message) {
        super(message);
    }

    public PartyException(Throwable cause) {
        super(cause);
    }

    public PartyException(String message, Throwable cause) {
        super(message, cause);
    }
}
