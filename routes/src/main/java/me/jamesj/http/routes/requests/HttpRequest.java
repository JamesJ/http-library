package me.jamesj.http.routes.requests;

import me.jamesj.http.library.parameters.ParameterMap;
import me.jamesj.http.routes.HttpMethod;

public interface HttpRequest {

    String requestId();

    HttpMethod method();

    String userAgent();

    String ipAddress();

    String path();

    String contentType();

    ParameterMap parameters();

}
