package me.jamesj.http.library.server.impl.vertx.tests;

import me.jamesj.http.library.server.AbstractRoute;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.response.impl.GenericResponse;
import me.jamesj.http.library.server.routes.HttpRequest;

import java.util.concurrent.CompletableFuture;

/**
 * Created by James on 10/12/2021
 */

public class TestHttpRoute extends AbstractRoute<GenericResponse<String>> {

    public static final String BODY = "Hello from /test-path";

    public TestHttpRoute() {
        super("/test-path", HttpMethod.GET);
    }

    @Override
    public CompletableFuture<GenericResponse<String>> handle(HttpRequest httpRequest) {
        return CompletableFuture.completedFuture(GenericResponse.of(200, BODY));
    }
}
