package com.rovas.forgram.fogram.exception;

/**
 * Created by Mohamed El Sayed
 */

public class ChatUserIdException extends RuntimeException {

    public ChatUserIdException() {
        super();
    }

    public ChatUserIdException(String message) {
        super(message);
    }

    public ChatUserIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatUserIdException(Throwable cause) {
        super(cause);
    }

}
