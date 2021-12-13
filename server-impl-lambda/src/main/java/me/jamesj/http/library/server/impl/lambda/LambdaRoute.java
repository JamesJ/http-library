package me.jamesj.http.library.server.impl.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import me.jamesj.http.library.server.AbstractRoute;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.impl.InternalHttpServerException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class LambdaRoute<T extends HttpResponse<?>> extends AbstractRoute<T> implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    public LambdaRoute(@NotNull String path, @NotNull HttpMethod method) {
        super(path, method);
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        HttpRequest httpRequest = new LambdaRequest(event, context);

        CompletableFuture<T> completableFuture;
        for (HttpFilter filter : filters()) {
            try {
                filter.filter(httpRequest).join();
            } catch (Throwable throwable) {
                completableFuture = CompletableFuture.failedFuture(throwable);
                // return here just to make sure there's no chance of invoking the handler
                return handleResult(httpRequest, completableFuture);
            }
        }

        completableFuture = handle(httpRequest);

        return handleResult(httpRequest, completableFuture);
    }

    private APIGatewayV2HTTPResponse handleResult(HttpRequest httpRequest, CompletableFuture<T> completableFuture) {
        HttpResponse<?> response;
        try {
            response = completableFuture.join();
        } catch (Throwable throwable) {
            if (throwable instanceof HttpResponse) {
                response = (HttpResponse<?>) throwable;
            } else {
                response = new InternalHttpServerException(throwable);
            }
        }

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(response.getStatusCode())
                .withIsBase64Encoded(response.isBase64Encoded())
                .withBody(response.build(httpRequest).toString())
                .build();
    }

}
