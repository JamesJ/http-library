package me.jamesj.http.library.server.response;


import me.jamesj.http.library.server.routes.HttpRequest;

public interface HttpResponse<T> {
    
    T build(HttpRequest request);
    
    int getStatusCode();
    
}