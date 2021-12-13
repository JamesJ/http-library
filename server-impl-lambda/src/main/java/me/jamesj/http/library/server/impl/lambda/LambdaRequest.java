package me.jamesj.http.library.server.impl.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.impl.EmptyBody;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.routes.HttpRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class LambdaRequest implements HttpRequest {

    private final APIGatewayV2HTTPEvent requestEvent;

    private final String id, userAgent, path, ipAddress, contentType;
    private final HttpMethod method;

    private final Map<String, String[]> headers, query;
    private final Map<String, String> pathParams;

    private Body body;

    public LambdaRequest(APIGatewayV2HTTPEvent requestEvent, Context context) {
        this.requestEvent = requestEvent;

        this.method = HttpMethod.valueOf(requestEvent.getRequestContext().getHttp().getMethod().toUpperCase());
        this.id = "req_awsl_" + context.getAwsRequestId();

        this.userAgent = requestEvent.getHeaders().get(HttpHeaders.USER_AGENT);
        this.contentType = requestEvent.getHeaders().get(HttpHeaders.CONTENT_TYPE);
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
            this.body = BodyReader.read(requestEvent.getBody(), requestEvent.getIsBase64Encoded(), MediaType.parse(contentType));
        } else {
            this.body = new EmptyBody();
        }
    }

    @Override
    public @NotNull Body body() {
        return this.body;
    }

    @Override
    public <T> @Nullable T get(@NotNull Parameter<T> parameter) {
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
}
