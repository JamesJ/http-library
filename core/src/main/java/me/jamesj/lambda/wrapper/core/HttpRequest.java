package me.jamesj.lambda.wrapper.core;

public interface HttpRequest {

    String path();

    String ipAddress();

    String protocol();

    String userAgent();

}
