package me.jamesj.http.routes;

import me.jamesj.http.library.parameters.ParameterHolder;
import me.jamesj.http.util.HttpMethod;

public interface HttpRequest extends ParameterHolder {
    
    String requestId();
    
    HttpMethod method();
    
    String userAgent();
    
    String ipAddress();
    
    String path();
    
    String contentType();
    
}
