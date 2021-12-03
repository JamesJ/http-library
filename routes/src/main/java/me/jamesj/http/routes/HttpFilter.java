package me.jamesj.http.routes;

import me.jamesj.http.library.parameters.Parameter;
import me.jamesj.http.library.parameters.validation.Failure;
import me.jamesj.http.routes.requests.HttpRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HttpFilter {

    @NotNull
    Logger getLogger();

    /**
     * @param httpRequest the request to filter
     * @return a CompletableFuture, returning null if the request should proceed, or an exception if it shouldnt - the exception will be parsed and presented to the end user
     */
    @NotNull
    CompletableFuture<@Nullable Void> filter(@NotNull HttpRequest httpRequest);

    class ParameterFilter implements HttpFilter {

        private final Parameter<?>[] parameters;
        private final Logger logger;

        public ParameterFilter(Parameter<?>[] parameters) {
            this.parameters = parameters;
            this.logger = LoggerFactory.getLogger(getClass());
        }

        @Override
        public @NotNull Logger getLogger() {
            return this.logger;
        }

        @Override
        public @NotNull CompletableFuture<@Nullable Void> filter(@NotNull HttpRequest httpRequest) {
            if (parameters == null || parameters.length == 0) {
                return CompletableFuture.completedFuture(null);
            }
            Map<Parameter<?>, Failure> missing = new HashMap<>();
            for (Parameter<?> parameter : this.parameters) {
                Object result = httpRequest.parameters().get(parameter);
                if (result == null && parameter.isRequired()) {
                    missing.put(parameter, Failure.of("Field not provided"));
                } else {

                }
            }
            if (missing.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            return null;
        }


    }

}
