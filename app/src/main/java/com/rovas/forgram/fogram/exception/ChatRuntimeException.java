package com.rovas.forgram.fogram.exception;

/**
 * Created by Mohamed El Sayed
 */

public class ChatRuntimeException extends RuntimeException {

    public ChatRuntimeException() {
        super();
    }

    public ChatRuntimeException(String message) {
        super(message);
    }

    public ChatRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatRuntimeException(Throwable cause) {
        super(cause);
    }

}
