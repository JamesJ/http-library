package me.jamesj.http.library.server.routes;

import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.parameters.ParameterHolder;
import me.jamesj.http.library.server.telemetry.Telemetry;

public interface HttpRequest extends ParameterHolder {

    String requestId();

    HttpMethod method();

    String userAgent();

    String ipAddress();

    String path();

    String contentType();

    <K> void with(String key, K k);

    boolean contains(String key);

    <K> K get(String key);

    Telemetry telemetry();
}
