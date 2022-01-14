package me.jamesj.http.library.server;

import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRoute;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public interface HttpServer {

    Logger getLogger();

    HttpConfiguration configuration();

    default <T extends HttpResponse> void register(HttpRoute<T> route, HttpFilter... filters) {
        register(route, Arrays.asList(filters));
    }

    <T extends HttpResponse> void register(HttpRoute<T> route, List<HttpFilter> filters);

    default <T extends HttpResponse> void register(AbstractRoute<T> route) {
        register(route, route.filters());
    }

}
