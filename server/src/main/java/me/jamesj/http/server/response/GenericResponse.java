package me.jamesj.http.server.response;

import me.jamesj.http.routes.HttpRequest;
import me.jamesj.http.routes.response.HttpResponse;

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
}
