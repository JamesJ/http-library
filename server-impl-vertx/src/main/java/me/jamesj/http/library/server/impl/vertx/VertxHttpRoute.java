package me.jamesj.http.library.server.impl.vertx;

import com.google.common.net.HttpHeaders;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.library.server.AbstractRoute;
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
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by James on 11/12/2021
 */

@SuppressWarnings("UnstableApiUsage")
public class VertxHttpRoute<T extends HttpResponse> implements Handler<RoutingContext> {
    private final VertxHttpServer server;
    private final HttpRoute<T> httpRoute;
    private final List<HttpFilter> filters;

    public VertxHttpRoute(VertxHttpServer server, HttpRoute<T> route, List<HttpFilter> filters) {
        this.server = server;
        this.httpRoute = route;
        this.filters = filters;
    }

    public VertxHttpRoute(VertxHttpServer server, AbstractRoute<T> route) {
        this(server, route, route.filters());
    }

    public HttpRequest buildHttpRequest(RoutingContext routingContext) {
        return new VertxHttpRequest(server.configuration().getRequestIdGenerator().get(), routingContext);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        CompletableFuture<T> completableFuture = null;
        HttpRequest httpRequest = buildHttpRequest(routingContext);
        try {
            httpRequest.load();

            for (HttpFilter filter : filters) {
                try {
                    filter.filter(httpRequest).join();
                } catch (Throwable throwable) {
                    completableFuture = CompletableFuture.failedFuture(throwable);
                    break;
                }
            }
            if (completableFuture == null) {
                completableFuture = this.httpRoute.handle(httpRequest);
            }
        } catch (BodyParsingException e) {
            completableFuture = CompletableFuture.failedFuture(e);
        } catch (ParsingException e) {
            completableFuture = CompletableFuture.failedFuture(new BadRequestException(Map.of(e.getParameter(), new Validator.Failure[]{e.getFailure()})));
        }

        handle(httpRoute.getLogger(), httpRequest, completableFuture, routingContext);
    }

    protected static <T extends HttpResponse> void handle(Logger logger, HttpRequest httpRequest, CompletableFuture<T> completableFuture, RoutingContext context) {
        completableFuture.whenComplete((t, throwable) -> {
            HttpResponse<?> httpResponse;
            if (throwable != null) {
                if (throwable.getCause() != null) {
                    throwable = throwable.getCause();
                }
                if (throwable instanceof HttpException) {
                    httpResponse = (HttpException) throwable;
                } else if (throwable instanceof ParsingException) {
                    ParsingException e = (ParsingException) throwable;
                    httpResponse = new BadRequestException(Map.of(e.getParameter(), new Validator.Failure[]{e.getFailure()}));
                } else {
                    InternalHttpServerException internalHttpServerException = new InternalHttpServerException(throwable);
                    logger.error("Caught exception (ID: " + internalHttpServerException.getId() + ") in request " + httpRequest.requestId(), throwable);
                    httpResponse = internalHttpServerException;
                }
            } else {
                httpResponse = (HttpResponse<?>) t;
            }
            context.response().putHeader(HttpHeaders.CONTENT_TYPE, httpResponse.getMediaType().toString());
            context.response().setStatusCode(httpResponse.getStatusCode());
            Object response;
            try {
                response = httpResponse.build(httpRequest);
            } catch (Exception e) {
                InternalHttpServerException cast = new InternalHttpServerException(e);
                response = cast.build(httpRequest);
                logger.error("Caught exception (ID: " + cast.getId() + ") when building response request " + httpRequest.requestId(), throwable);
            }

            context.response().end(response.toString());
        }).whenComplete((t, throwable) -> {
            long time = System.currentTimeMillis() - (long) context.get("start");
            logger.info("Handled request ({}) from {} in {}ms (status: {}, written: {})",
                    httpRequest.requestId(), httpRequest.ipAddress(), time, context.response().getStatusCode(), context.response().bytesWritten());
        });
    }
}
