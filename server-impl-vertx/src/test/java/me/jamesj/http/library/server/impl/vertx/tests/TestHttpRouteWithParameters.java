package me.jamesj.http.library.server.impl.vertx.tests;

import me.jamesj.http.library.server.AbstractRoute;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.response.impl.GenericResponse;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.HttpRoute;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Created by James on 10/12/2021
 */

public class TestHttpRouteWithParameters extends AbstractRoute<GenericResponse<String>> {

    public static final String BODY = "Hello from /test-path-with-parameters from ";

    private static final Parameter<String> PARAMETER_NAME = Parameter.string()
            .form("name")
            .required()
            .build();

    public TestHttpRouteWithParameters() {
        super("/test-path-with-parameters", HttpMethod.POST, PARAMETER_NAME);
    }

    @Override
    public CompletableFuture<GenericResponse<String>> handle(HttpRequest httpRequest) {
        return CompletableFuture.completedFuture(GenericResponse.of(200, BODY + httpRequest.get(PARAMETER_NAME)));
    }
}
