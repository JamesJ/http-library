package me.jamesj.http.library.server.impl.vertx;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import me.jamesj.http.library.server.HttpConfiguration;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.HttpServer;
import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.HttpRoute;
import me.jamesj.http.library.server.routes.exceptions.impl.RouteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VertxHttpServer implements HttpServer {

    private final Logger logger;
    private final HttpConfiguration configuration;
    private final Router router;

    private final io.vertx.core.http.HttpServer httpServer;

    public VertxHttpServer(HttpConfiguration configuration) {
        this.configuration = configuration;
        this.logger = LoggerFactory.getLogger(getClass());
        this.router = Router.router(Vertx.vertx());

        this.router.route().handler(BodyHandler.create(false)).handler(routingContext -> {
            routingContext.put("start", System.currentTimeMillis());
            routingContext.next();
        });

        HttpServerOptions options = new HttpServerOptions().setPort(configuration.getPort());
        this.httpServer = Vertx.vertx().createHttpServer(options);
    }

    static class UnknownRoute {

    }

    public void start() {
        Logger unknownRouteLogger = LoggerFactory.getLogger(UnknownRoute.class);
        router.route().handler(routingContext -> {
            String id = routingContext.get("_id");
            if (id == null) {
                id = configuration.getRequestIdGenerator().get();
            }
            VertxHttpRequest vertxHttpRequest = new VertxHttpRequest(id, routingContext);
            VertxHttpRoute.handle(unknownRouteLogger, vertxHttpRequest, CompletableFuture.failedFuture(new RouteNotFoundException()), routingContext);
        });


        Future<io.vertx.core.http.HttpServer> future = httpServer.requestHandler(router).listen();

        if (future.failed()) {
            getLogger().info("Failed to start http server on port {}", configuration.getPort(), future.cause());
        } else {
            getLogger().info("Started http server on port {}", httpServer.actualPort());
        }
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
    public <T extends HttpResponse> void register(HttpRoute<T> httpRoute, List<HttpFilter> filters) {
        io.vertx.core.http.HttpMethod method = toVertxHttpMethod(httpRoute.method());
        String path = httpRoute.path();

        Route route = router.route(method, path);

        VertxHttpRoute<T> vertxHttpRoute = new VertxHttpRoute<>(this, httpRoute, filters);

        route.blockingHandler(vertxHttpRoute);

        route.failureHandler(routingContext -> {
            String id = routingContext.get("id");
            HttpRequest httpRequest = new VertxHttpRequest(id, routingContext);

            VertxHttpRoute.handle(getLogger(), httpRequest, CompletableFuture.failedFuture(routingContext.failure()), routingContext);
        });
    }

    private io.vertx.core.http.HttpMethod toVertxHttpMethod(HttpMethod method) {
        return io.vertx.core.http.HttpMethod.valueOf(method.name());
    }

}
