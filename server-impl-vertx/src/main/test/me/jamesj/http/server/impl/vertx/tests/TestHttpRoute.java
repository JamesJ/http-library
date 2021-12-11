package me.jamesj.http.server.impl.vertx.tests;

import me.jamesj.http.routes.HttpRequest;
import me.jamesj.http.routes.HttpRoute;
import me.jamesj.http.server.response.GenericResponse;
import me.jamesj.http.util.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by James on 10/12/2021
 */

public class TestHttpRoute implements HttpRoute<GenericResponse<String>> {
    
    public static final String BODY = "Hello from /test-path";
    
    private final Logger logger = LoggerFactory.getLogger(TestHttpRoute.class);
    
    @Override
    public Logger getLogger() {
        return this.logger;
    }
    
    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }
    
    @Override
    public String path() {
        return "/test-path";
    }
    
    @Override
    public GenericResponse<String> handle(HttpRequest httpRequest) {
        return GenericResponse.of(200, BODY);
    }
}
