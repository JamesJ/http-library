package me.jamesj.http.library.server.body.exceptions.impl;

import me.jamesj.http.library.server.body.exceptions.BodyParsingException;

public class NoContentDispositionHeaderProvidedException extends BodyParsingException {

    public NoContentDispositionHeaderProvidedException() {
        super("No boundary provided for multipart/form-data");
    }
}
