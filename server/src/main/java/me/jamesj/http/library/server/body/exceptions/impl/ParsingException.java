package me.jamesj.http.library.server.body.exceptions.impl;

import me.jamesj.http.library.server.parameters.Validator;

public class ParsingException extends Exception {

    private final Validator.Failure failure;

    public ParsingException(Validator.Failure failure) {
        this.failure = failure;
    }

    public Validator.Failure getFailure() {
        return failure;
    }
}
