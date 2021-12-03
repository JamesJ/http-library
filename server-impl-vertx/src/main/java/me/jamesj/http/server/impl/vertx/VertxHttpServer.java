package me.jamesj.http.server.impl.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.routes.HttpFilter;
import me.jamesj.http.routes.HttpMethod;
import me.jamesj.http.routes.HttpRoute;
import me.jamesj.http.routes.exceptions.HttpException;
import me.jamesj.http.routes.exceptions.impl.InternalHttpServerException;
import me.jamesj.http.routes.requests.HttpRequest;
import me.jamesj.http.routes.response.HttpResponse;
import me.jamesj.http.server.HttpConfiguration;
import me.jamesj.http.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxHttpServer implements HttpServer {

    private final Logger logger;
    private final HttpConfiguration configuration;
    private final Router router;

    public VertxHttpServer(HttpConfiguration configuration) {
        this.configuration = configuration;
        this.logger = LoggerFactory.getLogger(getClass());
        this.router = Router.router(Vertx.vertx());
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public HttpConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public <T extends HttpResponse<T>> void register(HttpRoute<T> route, HttpFilter... filters) {
        io.vertx.core.http.HttpMethod method = toVertxHttpMethod(route.method());
        String path = route.path();

        for (HttpFilter filter : filters) {
            router.route(method, path).blockingHandler(new VertxHttpFilter(this, filter));
        }
        router.route(method, path);
    }

    private io.vertx.core.http.HttpMethod toVertxHttpMethod(HttpMethod method) {
        switch (method) {
            case GET:
                return io.vertx.core.http.HttpMethod.GET;
            case POST:
                return io.vertx.core.http.HttpMethod.POST;
            case DELETE:
                return io.vertx.core.http.HttpMethod.DELETE;
            case PUT:
                return io.vertx.core.http.HttpMethod.PUT;
            case OPTIONS:
                return io.vertx.core.http.HttpMethod.OPTIONS;
            case CONNECT:
                return io.vertx.core.http.HttpMethod.CONNECT;
            case PATCH:
                return io.vertx.core.http.HttpMethod.PATCH;
            case TRACE:
                return io.vertx.core.http.HttpMethod.TRACE;
            case HEAD:
                return io.vertx.core.http.HttpMethod.HEAD;
        }
        return io.vertx.core.http.HttpMethod.GET;
    }

    protected void handleException(Logger logger, HttpRequest httpRequest, RoutingContext routingContext, Throwable throwable) {
        HttpResponse<?> httpResponse;
        if (throwable instanceof HttpException) {
            httpResponse = ((HttpException) throwable);
        } else {
            InternalHttpServerException internalHttpServerException = new InternalHttpServerException(throwable);
            httpResponse = internalHttpServerException;
            logger.error("Caught error (id: {}) whilst executing request {} from {}", internalHttpServerException.getId(), httpRequest.requestId(), httpRequest.ipAddress(), internalHttpServerException);
        }

        routingContext.response().setStatusCode(httpResponse.getStatusCode());
        routingContext.response().write(httpResponse.build(httpRequest).toString());
    }
}
