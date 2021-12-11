package me.jamesj.http.library.server.impl.vertx.tests;

import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.HttpRoute;
import me.jamesj.http.library.server.response.GenericResponse;
import me.jamesj.http.library.server.HttpMethod;
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
