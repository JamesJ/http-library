package me.jamesj.http.library.server.impl.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.body.impl.EmptyBody;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.impl.BadRequestException;
import me.jamesj.http.library.server.telemetry.Telemetry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LambdaRequest implements HttpRequest {

    private final Telemetry telemetry;
    private final APIGatewayV2HTTPEvent requestEvent;

    private final Map<String, Object> map;

    private final String id, userAgent, path, ipAddress, contentType;
    private final HttpMethod method;

    private final Map<String, String[]> headers, query;
    private final Map<String, String> pathParams;

    private Body body;

    public LambdaRequest(Telemetry telemetry, HttpMethod httpMethod, APIGatewayV2HTTPEvent requestEvent, Context context) {
        LoggerFactory.getLogger(LambdaRequest.class).info("requestEvent={}", new Gson().toJson(requestEvent));
        this.telemetry = telemetry;
        this.map = new HashMap<>();

        this.requestEvent = requestEvent;

        this.method = httpMethod;
        this.id = "req_awsl_" + context.getAwsRequestId();

        this.userAgent = requestEvent.getHeaders().get("user-agent");
        this.contentType = requestEvent.getHeaders().get("content-type");
        this.ipAddress = requestEvent.getRequestContext().getHttp().getSourceIp();
        this.path = requestEvent.getRequestContext().getHttp().getPath();

        this.headers = new LinkedHashMap<>();
        Map<String, String> awsHeaders = this.requestEvent.getHeaders();
        if (awsHeaders != null) {
            this.requestEvent.getHeaders().forEach((s, s2) -> this.headers.put(s.toLowerCase(), new String[]{s2}));
        }

        this.query = new LinkedHashMap<>();
        Map<String, String> awsQueryParams = this.requestEvent.getQueryStringParameters();
        if (awsQueryParams != null) {
            this.requestEvent.getQueryStringParameters().forEach((s, s2) -> this.query.put(s, new String[]{s2}));
        }

        this.pathParams = requestEvent.getPathParameters();
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
    public void load() throws BodyParsingException {
        if (method().hasBodySupport()) {
            if (contentType() == null) {
                throw new BodyParsingException("No Content-Type provided!");
            }
            this.body = BodyReader.read(requestEvent.getBody(), requestEvent.getIsBase64Encoded(), MediaType.parse(contentType), StandardCharsets.UTF_8);
        } else {
            this.body = new EmptyBody();
        }
    }

    @Override
    public @NotNull Body body() {
        return this.body;
    }

    @Override
    public <T> @Nullable T get(@NotNull Parameter<T> parameter) throws ParsingException {
        return parameter.fetch(this);
    }

    @Override
    public String requestId() {
        return this.id;
    }

    @Override
    public HttpMethod method() {
        return method;
    }

    @Override
    public String userAgent() {
        return this.userAgent;
    }

    @Override
    public String ipAddress() {
        return this.ipAddress;
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public String contentType() {
        return this.contentType;
    }

    @Override
    public <K> void with(String key, K k) {
        this.map.put(key, k);
    }

    @Override
    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    @Override
    public <K> K get(String key) {
        return (K) this.map.get(key);
    }

    @Override
    public Telemetry telemetry() {
        return this.telemetry;
    }
}
