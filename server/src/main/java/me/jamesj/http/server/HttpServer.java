package me.jamesj.http.server;

import me.jamesj.http.routes.HttpFilter;
import me.jamesj.http.routes.HttpRoute;
import me.jamesj.http.routes.response.HttpResponse;
import org.slf4j.Logger;

public interface HttpServer {

    Logger getLogger();

    HttpConfiguration configuration();

    <T extends HttpResponse<T>> void register(HttpRoute<T> route, HttpFilter... filters);

}
