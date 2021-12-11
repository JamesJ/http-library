package me.jamesj.http.library.server.routes;

import me.jamesj.http.library.server.parameters.ParameterHolder;
import me.jamesj.http.library.server.HttpMethod;

public interface HttpRequest extends ParameterHolder {
    
    String requestId();
    
    HttpMethod method();
    
    String userAgent();
    
    String ipAddress();
    
    String path();
    
    String contentType();
    
}
