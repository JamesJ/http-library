package me.jamesj.lambda.wrapper.core;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

public class LambdaHttpRequest implements HttpRequest {

    private final APIGatewayV2HTTPEvent.RequestContext context;

    public LambdaHttpRequest(APIGatewayV2HTTPEvent.RequestContext context) {
        this.context = context;
    }

    @Override
    public String path() {
        return context.getHttp().getPath();
    }

    @Override
    public String protocol() {
        return context.getHttp().getProtocol();
    }

    @Override
    public String userAgent() {
        return context.getHttp().getUserAgent();
    }

    @Override
    public String ipAddress() {
        return context.getHttp().getSourceIp();
    }
}
