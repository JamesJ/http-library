package me.jamesj.http.server.impl.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.routes.HttpFilter;
import me.jamesj.http.routes.requests.HttpRequest;

public class VertxHttpFilter implements Handler<RoutingContext> {

    private final VertxHttpServer httpServer;
    private final HttpFilter httpFilter;

    public VertxHttpFilter(VertxHttpServer httpServer, HttpFilter httpFilter) {
        this.httpServer = httpServer;
        this.httpFilter = httpFilter;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpRequest httpRequest = new VertxHttpRequest(routingContext);
        this.httpFilter.filter(httpRequest).whenComplete((o, throwable) -> {
            if (throwable != null) {
                httpServer.handleException(httpFilter.getLogger(), httpRequest, routingContext, throwable);
            } else {
                routingContext.next();
            }
        });
    }
}
