package me.jamesj.http.server.impl.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import me.jamesj.http.routes.*;
import me.jamesj.http.routes.exceptions.HttpException;
import me.jamesj.http.routes.exceptions.impl.InternalHttpServerException;
import me.jamesj.http.routes.response.HttpResponse;
import me.jamesj.http.server.HttpConfiguration;
import me.jamesj.http.server.HttpServer;
import me.jamesj.http.util.HttpMethod;
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
        
        Vertx.vertx().createHttpServer(new HttpServerOptions()
            .setPort(configuration.getPort()));
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
    public <K, T extends HttpResponse<K>> void register(HttpRoute<T> route, HttpFilter... filters) {
        io.vertx.core.http.HttpMethod method = toVertxHttpMethod(route.method());
        String path = route.path();
        
        router.route(method, path).blockingHandler(BodyHandler.create(false));
        for (HttpFilter filter : filters) {
            router.route(method, path).blockingHandler(new VertxHttpFilter(this, filter));
        }
        router.route(method, path).handler(new VertxHttpRoute(this, route));
        
    }
    
    private io.vertx.core.http.HttpMethod toVertxHttpMethod(HttpMethod method) {
        return io.vertx.core.http.HttpMethod.valueOf(method.name());
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
