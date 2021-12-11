package me.jamesj.http.library.body.impl;

import me.jamesj.http.library.body.Body;
import me.jamesj.http.library.body.BodyReader;
import me.jamesj.http.library.body.exceptions.BodyParsingException;
import me.jamesj.http.library.parameters.v2.Parameter;
import me.jamesj.http.library.parameters.v2.Source;

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
