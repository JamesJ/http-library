package me.jamesj.http.library.server.impl.vertx;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import io.vertx.ext.web.RoutingContext;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.body.impl.EmptyBody;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.routes.HttpRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VertxHttpRequest implements HttpRequest {

    private final String id;

    private final RoutingContext routingContext;
    private final Map<String, String[]> headers;
    private final Map<String, String[]> query;
    private final Map<String, String> pathParams;
    private Body body;

    private Charset charset;

    public VertxHttpRequest(String id, RoutingContext routingContext) {
        this.routingContext = routingContext;

        this.id = id;

        this.headers = new HashMap<>();
        this.query = new HashMap<>();
        this.pathParams = new HashMap<>(routingContext.pathParams());

        for (Map.Entry<String, String> header : routingContext.request().headers()) {
            this.headers.compute(header.getKey().toLowerCase(), (s, strings) -> {
                if (strings == null) {
                    strings = new String[0];
                }
                List<String> list = new ArrayList<>(Arrays.asList(strings));
                list.add(s);

                return list.toArray(String[]::new);
            });
        }

        for (Map.Entry<String, String> query : routingContext.queryParams()) {
            this.query.compute(query.getKey().toLowerCase(), (s, strings) -> {
                if (strings == null) {
                    strings = new String[0];
                }
                List<String> list = new ArrayList<>(Arrays.asList(strings));
                list.add(s);

                return list.toArray(String[]::new);
            });
        }

        this.charset = StandardCharsets.UTF_8;
    }

    @Override
    public void load() throws BodyParsingException {
        if (method().hasBodySupport()) {
            String rawBody = routingContext.getBodyAsString();
            this.body = BodyReader.read(rawBody, false, MediaType.parse(routingContext.parsedHeaders().contentType().component()), charset);
        } else {
            this.body = new EmptyBody();
        }
    }

    @Override
    public @NotNull Body body() {
        return this.body;
    }

    @Override
    public String requestId() {
        return this.id;
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
        return routingContext.request().path();
    }

    @Override
    public String contentType() {
        return routingContext.request().headers().get(HttpHeaders.CONTENT_TYPE);
    }

    @Override
    public @NotNull Map<String, String[]> headers() {
        return this.headers;
    }

    @Override
    public @NotNull Map<String, String[]> query() {
        return this.query;
    }

    @Override
    public @NotNull Map<String, String> pathParams() {
        return this.pathParams;
    }

    @Override
    public <T> @Nullable T get(@NotNull Parameter<T> parameter) throws ParsingException {
        return parameter.fetch(this);
    }

    @Override
    public <K> void with(String key, K k) {
        routingContext.put(key, k);
    }

    @Override
    public boolean contains(String key) {
        return routingContext.data().containsKey(key);
    }

    @Override
    public <K> K get(String key) {
        return routingContext.get(key);
    }

}
