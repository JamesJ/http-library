package me.jamesj.http.library.server.body.exceptions.impl;

import me.jamesj.http.library.server.body.exceptions.BodyParsingException;

public class NoEntryNameProvidedException extends BodyParsingException {

    public NoEntryNameProvidedException() {
        super("No boundary provided for multipart/form-data");
    }
}
