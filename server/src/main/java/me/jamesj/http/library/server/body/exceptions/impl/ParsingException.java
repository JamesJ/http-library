package me.jamesj.http.library.server.body.exceptions.impl;

import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.Validator;

public class ParsingException extends RuntimeException {

    private final Parameter<?> parameter;
    private final Validator.Failure failure;

    public ParsingException(Parameter<?> parameter, Validator.Failure failure) {
        this.parameter = parameter;
        this.failure = failure;
    }

    public Parameter<?> getParameter() {
        return parameter;
    }

    public Validator.Failure getFailure() {
        return failure;
    }
}
