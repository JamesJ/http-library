package me.jamesj.http.library.server.impl.vertx.tests;

import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.HttpRoute;
import me.jamesj.http.library.server.response.impl.GenericResponse;
import me.jamesj.http.library.server.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by James on 10/12/2021
 */

public class TestHttpRouteWithParameters implements HttpRoute<GenericResponse<String>> {
    
    public static final String BODY = "Hello from /test-path-with-parameters";
    
    private static final Parameter<String> PARAMETER_NAME = Parameter.of("name");
    
    private final Logger logger = LoggerFactory.getLogger(TestHttpRouteWithParameters.class);
    
    @Override
    public Logger getLogger() {
        return this.logger;
    }
    
    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }
    
    @Override
    public String path() {
        return "/test-path-with-parameters";
    }
    
    @Override
    public Collection<Parameter<?>> parameters() {
        return Collections.singleton(PARAMETER_NAME);
    }
    
    @Override
    public GenericResponse<String> handle(HttpRequest httpRequest) {
        return GenericResponse.of(200, BODY);
    }
}
