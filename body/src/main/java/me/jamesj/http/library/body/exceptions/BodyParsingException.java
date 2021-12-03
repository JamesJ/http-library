package me.jamesj.http.library.body.exceptions;

public abstract class BodyParsingException extends Exception {

    public BodyParsingException(String message) {
        super(message);
    }
}
