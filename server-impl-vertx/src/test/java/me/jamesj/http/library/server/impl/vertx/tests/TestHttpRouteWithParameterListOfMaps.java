package me.jamesj.http.library.server.impl.vertx.tests;

import me.jamesj.http.library.server.AbstractRoute;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.parser.Parser;
import me.jamesj.http.library.server.response.impl.GenericResponse;
import me.jamesj.http.library.server.routes.HttpRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by James on 10/12/2021
 */

public class TestHttpRouteWithParameterListOfMaps extends AbstractRoute<GenericResponse<String>> {

    public static final String BODY = "Correct";

    private static final Parameter<List<Map<String, String>>> LIST_OF_STRINGS = Parameter.list(Parser.asMap())
            .form("list")
            .required()
            .build();

    public TestHttpRouteWithParameterListOfMaps() {
        super("/test-path-with-parameter-list-of-maps", HttpMethod.POST, LIST_OF_STRINGS);
    }

    @Override
    public CompletableFuture<GenericResponse<String>> handle(HttpRequest httpRequest) {
        List<Map<String, String>> list = httpRequest.get(LIST_OF_STRINGS);
        for (int i = 0; i < list.size(); i++) {
            getLogger().info("list[{}] = {}", i, list.get(i));
        }
        return CompletableFuture.completedFuture(GenericResponse.of(200, BODY));
    }
}
