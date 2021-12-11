package me.jamesj.http.library.server.body.impl;

import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.parameters.v2.Source;

public class FormDataBody implements Body {
    
    @Override
    public Source.Result get(String key) {
        return null;
    }
    
    @Override
    public int length() {
        return 0;
    }
    
    public static class FormDataReader implements BodyReader {
        
        @Override
        public Body read(String body, boolean isBase64) throws BodyParsingException {
            return null;
        }
    }
}
