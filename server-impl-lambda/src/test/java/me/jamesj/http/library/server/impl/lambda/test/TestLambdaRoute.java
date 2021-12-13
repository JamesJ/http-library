package me.jamesj.http.library.server.impl.lambda.test;

import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.impl.lambda.LambdaRoute;
import me.jamesj.http.library.server.routes.HttpRequest;

import java.util.concurrent.CompletableFuture;

public class TestLambdaRoute extends LambdaRoute<TestResponse> {
    public TestLambdaRoute() {
        super("/lambda-test", HttpMethod.GET);
    }

    @Override
    public CompletableFuture<TestResponse> handle(HttpRequest httpRequest) {
        return CompletableFuture.completedFuture(new TestResponse("Hello from the TestLambdaRoute"));
    }
}
