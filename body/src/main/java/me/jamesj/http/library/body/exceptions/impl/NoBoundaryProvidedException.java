package me.jamesj.http.library.body.exceptions.impl;

import me.jamesj.http.library.body.exceptions.BodyParsingException;

public class NoBoundaryProvidedException extends BodyParsingException {

    public NoBoundaryProvidedException() {
        super("No boundary provided for multipart/form-data");
    }
}
