package me.jamesj.http.library.server.body.exceptions.impl;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;

public class UnknownContentTypeException extends BodyParsingException {

    public UnknownContentTypeException(MediaType mediaType) {
        super("Failed to parse body due to unknown media type: " + mediaType);
    }
}
