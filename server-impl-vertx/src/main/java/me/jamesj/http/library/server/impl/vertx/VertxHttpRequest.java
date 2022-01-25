package me.jamesj.http.library.server.impl.vertx;

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
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VertxHttpRequest implements HttpRequest {

    private final String id;

    private final RoutingContext routingContext;
    private final Map<String, String[]> headers;
    private final Map<String, String[]> query;
    private final Map<String, String> pathParams;
    private Body body;

    private final String userAgent, contentType;

    private final HttpMethod method;
    private final Charset charset;

    public VertxHttpRequest(String id, RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.routingContext.put("_id", id);
        this.method = HttpMethod.valueOf(routingContext.request().method().name().toUpperCase());

        this.id = id;

        this.headers = new CaseInsensitiveMap<>();
        this.query = new CaseInsensitiveMap<>();
        this.pathParams = new CaseInsensitiveMap<>(routingContext.pathParams());

        for (Map.Entry<String, String> header : routingContext.request().headers()) {
            this.headers.compute(header.getKey().toLowerCase(), (s, strings) -> {
                if (strings == null) {
                    strings = new String[0];
                }
                List<String> list = new ArrayList<>(Arrays.asList(strings));
                list.add(header.getValue());

                return list.toArray(String[]::new);
            });
        }

        for (Map.Entry<String, String> query : routingContext.queryParams()) {
            this.query.compute(query.getKey().toLowerCase(), (s, strings) -> {
                if (strings == null) {
                    strings = new String[0];
                }
                List<String> list = new ArrayList<>(Arrays.asList(strings));
                list.add(query.getValue());

                return list.toArray(String[]::new);
            });
        }

        this.charset = StandardCharsets.UTF_8;
        this.userAgent = first(this.headers.get("user-agent"));
        this.contentType = first(this.headers.get("content-type"));
    }

    private String first(String[] headers) {
        if (headers == null || headers.length == 0) {
            return null;
        }
        return headers[0];

    }

    @Override
    public void load() throws BodyParsingException {
        if (method.hasBodySupport()) {
            MediaType mediaType = MediaType.parse(contentType());
            String rawBody = routingContext.getBodyAsString();
            this.body = BodyReader.read(rawBody, mediaType, charset);
        } else {
            this.body = new EmptyBody();
        }
    }

    public RoutingContext getRoutingContext() {
        return routingContext;
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
        return this.method;
    }

    @Override
    public String userAgent() {
        return this.userAgent;
    }

    @Override
    public String ipAddress() {
        return routingContext.request().connection().remoteAddress().hostAddress();
    }

    @Override
    public String path() {
        return routingContext.request().path();
    }

    @Override
    public String contentType() {
        return this.contentType;
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

    @Override
    public String toString() {
        return "VertxHttpRequest{" +
                "id='" + id + '\'' +
                ", routingContext=" + routingContext +
                ", headers=" + headers +
                ", query=" + query +
                ", pathParams=" + pathParams +
                ", body=" + body +
                ", userAgent='" + userAgent + '\'' +
                ", contentType='" + contentType + '\'' +
                ", method=" + method +
                ", charset=" + charset +
                '}';
    }
}
