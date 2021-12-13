package me.jamesj.http.library.server.parameters;

import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by James on 11/12/2021
 */

public interface ParameterHolder {
    
    @NotNull
    Map<String, String[]> headers();
    
    @NotNull
    Map<String, String[]> query();
    
    @NotNull
    Map<String, String> pathParams();
    
    void load() throws BodyParsingException;
    
    @NotNull
    Body body();
    
    @Nullable <T> T get(@NotNull Parameter<T> parameter) throws ParsingException;
}
