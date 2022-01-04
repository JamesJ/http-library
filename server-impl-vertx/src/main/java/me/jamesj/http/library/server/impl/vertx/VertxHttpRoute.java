package me.jamesj.http.library.server.impl.vertx;

import com.google.common.net.HttpHeaders;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.Validator;
import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.HttpRoute;
import me.jamesj.http.library.server.routes.exceptions.HttpException;
import me.jamesj.http.library.server.routes.exceptions.impl.BadRequestException;
import me.jamesj.http.library.server.routes.exceptions.impl.InternalHttpServerException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by James on 11/12/2021
 */

@SuppressWarnings("UnstableApiUsage")
public class VertxHttpRoute<K, T extends HttpResponse<K>> implements Handler<RoutingContext> {
    private final VertxHttpServer server;
    private final HttpRoute<T> httpRoute;
    private final List<HttpFilter> filters;

    public VertxHttpRoute(VertxHttpServer server, HttpRoute<T> route, List<HttpFilter> filters) {
        this.server = server;
        this.httpRoute = route;
        this.filters = filters;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        CompletableFuture<T> completableFuture = null;
        HttpRequest httpRequest = new VertxHttpRequest(server.configuration().getRequestIdGenerator().get(), routingContext);
        try {
            httpRequest.load();

            boolean broke = false;
            for (HttpFilter filter : filters) {
                try {
                    filter.filter(httpRequest).join();
                } catch (Throwable throwable) {
                    completableFuture = CompletableFuture.failedFuture(throwable);
                    broke = true;
                }
            }
            if (!broke) {
                completableFuture = this.httpRoute.handle(httpRequest);
            }
        } catch (BodyParsingException e) {
            completableFuture = CompletableFuture.failedFuture(e);
        } catch (ParsingException e) {
            completableFuture = CompletableFuture.failedFuture(new BadRequestException(Map.of(e.getParameter(), new Validator.Failure[]{e.getFailure()})));
        }

        try {

            handle(httpRequest, completableFuture, routingContext);
        } catch (Throwable throwable) {
            completableFuture = CompletableFuture.failedFuture(throwable);
        }
    }

    public void handle(HttpRequest httpRequest, CompletableFuture<T> completableFuture, RoutingContext context) {
        completableFuture.whenComplete((t, throwable) -> {
            HttpResponse<?> httpResponse;
            if (throwable != null) {
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
            context.response().putHeader(HttpHeaders.CONTENT_TYPE, httpResponse.getMediaType().toString());
            context.response().write(httpResponse.build(httpRequest).toString());
            context.next();
        });
    }
}
