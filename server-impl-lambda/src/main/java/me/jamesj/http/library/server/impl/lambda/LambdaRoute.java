package me.jamesj.http.library.server.impl.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.common.net.HttpHeaders;
import me.jamesj.http.library.server.AbstractRoute;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.Validator;
import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.impl.InternalHttpServerException;
import me.jamesj.http.library.server.routes.exceptions.impl.MissingParametersException;
import me.jamesj.http.library.server.telemetry.Telemetry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@SuppressWarnings("UnstableApiUsage")
public abstract class LambdaRoute<T extends HttpResponse<?>> extends AbstractRoute<T> implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    public LambdaRoute(@NotNull String path, @NotNull HttpMethod method) {
        super(path, method);
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        Telemetry telemetry = new Telemetry.TelemetryImpl();
        telemetry.jinx("Request start");

        HttpRequest httpRequest = new LambdaRequest(telemetry, method(), event, context);
        CompletableFuture<T> completableFuture;

        try {
            httpRequest.load();
        } catch (BodyParsingException e) {
            completableFuture = CompletableFuture.failedFuture(e);
            return handleResult(httpRequest, completableFuture);
        }

        telemetry.jinx("Iterate filters");
        for (int i = 0; i < filters().size(); i++) {
            HttpFilter filter = filters().get(i);
            telemetry.jinx("Filters[" + i + "] start");
            try {
                filter.filter(httpRequest).join();
            } catch (CompletionException completionException) {
                getLogger().info("Hit completion exception", completionException);
                completableFuture = CompletableFuture.failedFuture(completionException.getCause());
                // return here just to make sure there's no chance of invoking the handler
                return handleResult(httpRequest, completableFuture);
            } catch (Throwable throwable) {
                getLogger().info("Hit throwable", throwable);
                completableFuture = CompletableFuture.failedFuture(throwable);
                // return here just to make sure there's no chance of invoking the handler
                return handleResult(httpRequest, completableFuture);
            } finally {
                telemetry.jinx("Filters[" + i + "] finish");
            }
        }


        try {
            telemetry.jinx("Handler start");
            completableFuture = handle(httpRequest);
        } catch (ParsingException e) {
            completableFuture = CompletableFuture.failedFuture(new MissingParametersException(Map.of(e.getParameter(), new Validator.Failure[]{e.getFailure()})));
        } catch (Throwable throwable) {
            completableFuture = CompletableFuture.failedFuture(throwable);
        } finally {
            telemetry.jinx("Handler finish");
        }

        try {
            return handleResult(httpRequest, completableFuture);
        } finally {
            offloadTelemetry(telemetry);
        }
    }

    private void offloadTelemetry(Telemetry telemetry) {

    }

    private APIGatewayV2HTTPResponse handleResult(HttpRequest httpRequest, CompletableFuture<T> completableFuture) {
        try {
            httpRequest.telemetry().jinx("Build result");
            HttpResponse<?> response;
            try {
                try {
                    httpRequest.telemetry().jinx("Requested completed future");
                    response = completableFuture.join();
                } finally {
                    httpRequest.telemetry().jinx("Completed future finished");
                }
                if (response == null) {
                    response = new InternalHttpServerException("Internal server error", new NullPointerException("Response was null?"));
                }
            } catch (Throwable throwable) {
                if (throwable instanceof CompletionException) {
                    throwable = throwable.getCause();
                }

                if (throwable instanceof HttpResponse) {
                    response = (HttpResponse<?>) throwable;
                } else {
                    throwable.printStackTrace();

                    InternalHttpServerException internalHttpServerException = new InternalHttpServerException(throwable);
                    getLogger().error("Caught exception (ID: {}) in request {}", internalHttpServerException.getId(), httpRequest.requestId(), throwable);

                    response = internalHttpServerException;
                }
            }

            return APIGatewayV2HTTPResponse.builder()
                    .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, response.getMediaType().toString()))
                    .withStatusCode(response.getStatusCode())
                    .withIsBase64Encoded(response.isBase64Encoded())
                    .withBody(response.build(httpRequest).toString())
                    .build();
        } finally {
            httpRequest.telemetry().jinx("Finished building result");
        }
    }

}
