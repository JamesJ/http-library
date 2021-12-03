package me.jamesj.http.server.impl.vertx;

import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.library.parameters.ParameterMap;
import me.jamesj.http.routes.HttpMethod;
import me.jamesj.http.routes.requests.HttpRequest;

public class VertxHttpRequest implements HttpRequest {

    private final RoutingContext routingContext;

    public VertxHttpRequest(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public String requestId() {
        return null;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.valueOf(routingContext.request().method().name());
    }

    @Override
    public String userAgent() {
        return routingContext.request().headers().get("User-Agent");
    }

    @Override
    public String ipAddress() {
        return routingContext.request().host();
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public ParameterMap parameters() {
        return null;
    }
}
