package me.jamesj.http.library.server.impl.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.HttpRoute;
import me.jamesj.http.library.server.routes.exceptions.HttpException;
import me.jamesj.http.library.server.routes.exceptions.impl.InternalHttpServerException;
import me.jamesj.http.library.server.response.HttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Created by James on 11/12/2021
 */

public class VertxHttpRoute<K, T extends HttpResponse<K>> implements Handler<RoutingContext> {
    private final VertxHttpServer server;
    private final HttpRoute<T> httpRoute;
    
    public VertxHttpRoute(VertxHttpServer server, HttpRoute<T> route) {
        this.server = server;
        this.httpRoute = route;
    }
    
    @Override
    public void handle(RoutingContext routingContext) {
        CompletableFuture<T> completableFuture;
        HttpRequest httpRequest = new VertxHttpRequest(routingContext);
        try {
            httpRequest.load();
            completableFuture = this.httpRoute.handle(httpRequest);
        } catch (BodyParsingException e) {
            completableFuture = CompletableFuture.failedFuture(e);
        }
        
        handle(httpRequest, completableFuture, routingContext);
    }
    
    public void handle(HttpRequest httpRequest, CompletableFuture<T> completableFuture, RoutingContext context) {
        completableFuture.whenComplete((t, throwable) -> {
            HttpResponse httpResponse;
            if (throwable != null) {
                HttpException httpException;
                if (throwable instanceof HttpException) {
                    httpResponse = (HttpException) throwable;
                } else {
                    InternalHttpServerException internalHttpServerException = new InternalHttpServerException(throwable);
                    httpRoute.getLogger().error("Caught exception " + throwable + " (ID: " + internalHttpServerException.getId() + ") in request " + httpRequest.requestId());
                    httpResponse = internalHttpServerException;
                }
            } else {
                httpResponse = t;
            }
            context.response().write(httpResponse.build(httpRequest).toString());
            context.next();
        });
    }
}
