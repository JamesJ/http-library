package me.jamesj.http.library.body.exceptions.impl;

import me.jamesj.http.library.body.exceptions.BodyParsingException;

public class NoContentDispositionHeaderProvidedException extends BodyParsingException {

    public NoContentDispositionHeaderProvidedException() {
        super("No boundary provided for multipart/form-data");
    }
}
