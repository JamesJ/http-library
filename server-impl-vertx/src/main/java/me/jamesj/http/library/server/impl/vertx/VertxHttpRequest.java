package me.jamesj.http.library.server.impl.vertx;

import com.google.common.net.MediaType;
import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.parameters.v2.Parameter;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VertxHttpRequest implements HttpRequest {
    
    private final RoutingContext routingContext;
    private final Map<String, String[]> headers;
    private final Map<String, String[]> query;
    private final Map<String, String> pathParams;
    private Body body;
    
    public VertxHttpRequest(RoutingContext routingContext) {
        this.routingContext = routingContext;
        
        this.headers = new LinkedHashMap<>();
        this.query = new LinkedHashMap<>();
        this.pathParams = routingContext.pathParams();
        
        for (Map.Entry<String, String> header : routingContext.request().headers()) {
            this.headers.compute(header.getKey().toLowerCase(), (s, strings) -> {
                List<String> list = new ArrayList<>(Arrays.asList(strings));
                list.add(s);
                
                return list.toArray(String[]::new);
            });
        }
        
        for (Map.Entry<String, String> query : routingContext.queryParams()) {
            this.query.compute(query.getKey().toLowerCase(), (s, strings) -> {
                List<String> list = new ArrayList<>(Arrays.asList(strings));
                list.add(s);
                
                return list.toArray(String[]::new);
            });
        }
    }
    
    @Override
    public void load() throws BodyParsingException {
        String rawBody = routingContext.getBodyAsString();
        this.body = BodyReader.read(rawBody, false, MediaType.parse(routingContext.parsedHeaders().contentType().component()));
    }
    
    @Override
    public @NotNull Body body() {
        return this.body;
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
    public Map<String, String[]> headers() {
        return this.headers;
    }
    
    @Override
    public Map<String, String[]> query() {
        return this.query;
    }
    
    @Override
    public Map<String, String> pathParams() {
        return this.pathParams;
    }
    
    @Override
    public <T> @Nullable T get(@NotNull Parameter<T> parameter) {
        return parameter.fetch(this);
    }
}
