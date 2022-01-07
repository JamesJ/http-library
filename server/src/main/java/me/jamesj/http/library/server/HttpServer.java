package me.jamesj.http.library.server;

import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRoute;
import me.jamesj.http.library.server.response.HttpResponse;
import org.slf4j.Logger;

public interface HttpServer {

    Logger getLogger();

    HttpConfiguration configuration();

    <T extends HttpResponse> void register(HttpRoute<T> route, HttpFilter... filters);

}
