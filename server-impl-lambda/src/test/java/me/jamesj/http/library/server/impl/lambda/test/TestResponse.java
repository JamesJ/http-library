package me.jamesj.http.library.server.impl.lambda.test;

import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpRequest;

public class TestResponse implements HttpResponse<String> {

    private final String body;

    public TestResponse(String body) {
        this.body = body;
    }

    @Override
    public String build(HttpRequest request) {
        return this.body;
    }

    @Override
    public int getStatusCode() {
        return 200;
    }
}
