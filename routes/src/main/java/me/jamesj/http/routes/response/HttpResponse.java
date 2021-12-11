package me.jamesj.http.routes.response;


import me.jamesj.http.routes.HttpRequest;

public interface HttpResponse<T> {
    
    T build(HttpRequest request);
    
    int getStatusCode();
    
}
