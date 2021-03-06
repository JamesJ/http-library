package me.jamesj.http.library.server.response.impl;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpRequest;

/**
 * Created by James on 10/12/2021
 */

public final class GenericResponse<T> implements HttpResponse<T> {
    
    private final T t;
    private final int statusCode;
    
    public GenericResponse(int statusCode, T t) {
        this.t = t;
        this.statusCode = statusCode;
    }
    
    public static <T> GenericResponse<T> of(int statusCode, T t) {
        return new GenericResponse<>(statusCode, t);
    }
    
    @Override
    public T build(HttpRequest request) {
        return this.t;
    }
    
    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.ANY_TYPE;
    }
}
