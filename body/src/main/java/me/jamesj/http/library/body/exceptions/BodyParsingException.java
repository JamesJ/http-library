package me.jamesj.http.library.body.exceptions;

public class BodyParsingException extends Exception {

    public BodyParsingException(String message) {
        super(message);
    }
    
    public BodyParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
