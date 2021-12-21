package me.jamesj.http.library.server.parameters;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.files.File;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.impl.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public interface Validator<T> {

    static <T> Validator<T> min(int length) {
        return new MinimumLengthValidator(length);
    }

    static <T> Validator<T> max(int length) {
        return new MaximumLengthValidator(length);
    }

    static Validator<File> type(MediaType... types) {
        return new MediaTypeValidator(types);
    }

    @Nullable
    Failure test(@Nullable T t);

    class MinimumLengthValidator implements Validator {

        private final int value;
        private final String failureMessage;

        /**
         * @param value          - the desired minimum length
         * @param failureMessage - the failure message, can use the parameters  minimum length and current length via %s
         */
        public MinimumLengthValidator(int value, String failureMessage) {
            this.value = value;
            this.failureMessage = failureMessage;
        }

        /**
         * @param value - the desired minimum length
         */
        public MinimumLengthValidator(int value) {
            this(value, "Value is too short, must be a minimum of %s (current: %s)");
        }

        @Override
        public @Nullable Failure test(@Nullable Object o) {
            if (o instanceof Number) {
                Number n = (Number) o;
                if (n.intValue() < value) {
                    return Failure.of(String.format(failureMessage, value, n.intValue()));
                }
            }
            if (o instanceof String) {
                String s = (String) o;
                if (s.length() < value) {
                    return Failure.of(String.format(failureMessage, value, s.length()));
                }
            }
            if (o instanceof File) {
                File file = (File) o;
                if (file.content().length < value) {
                    return Failure.of(String.format(failureMessage, value, file.content().length));
                }
            }
            return null;
        }
    }

    class MaximumLengthValidator implements Validator {

        private final int value;
        private final String failureMessage;

        /**
         * @param value          - the desired maximum length
         * @param failureMessage - the failure message, can use the parameters maximum length and current length via %s
         */
        public MaximumLengthValidator(int value, String failureMessage) {
            this.value = value;
            this.failureMessage = failureMessage;
        }

        /**
         * @param value - the desired maximum length
         */
        public MaximumLengthValidator(int value) {
            this(value, "Value is too long, must be a maximum of %s (current: %s)");
        }

        @Override
        public @Nullable Failure test(@Nullable Object o) {
            if (o instanceof Number) {
                Number n = (Number) o;
                if (n.intValue() > value) {
                    return Failure.of(String.format(failureMessage, value, n.intValue()));
                }
            }
            if (o instanceof String) {
                String s = (String) o;
                if (s.length() > value) {
                    return Failure.of(String.format(failureMessage, value, s.length()));
                }
            }
            if (o instanceof File) {
                File file = (File) o;
                if (file.content().length > value) {
                    return Failure.of(String.format(failureMessage, value, file.content().length));
                }
            }
            return null;
        }
    }

    class MediaTypeValidator implements Validator<File> {

        private final MediaType[] value;
        private final String failureMessage;

        /**
         * @param value          - the desired maximum length
         * @param failureMessage - the failure message, can use the parameters allowed and current via %s
         */
        public MediaTypeValidator(MediaType[] value, String failureMessage) {
            this.value = value;
            this.failureMessage = failureMessage;
        }

        /**
         * @param value - the desired maximum length
         */
        public MediaTypeValidator(MediaType[] value) {
            this(value, "Media type is not allowed, only accepts %s (current: %s)");
        }

        @Override
        public @Nullable Failure test(@Nullable File file) {
            for (MediaType mediaType : value) {
                if (mediaType == file.mediaType()) {
                    return null;
                }
            }
            return Failure.of(String.format(failureMessage, allowed(), file.mediaType().toString()));
        }

        private String allowed() {
            return Arrays.stream(value).map(MediaType::toString).collect(Collectors.joining(","));
        }
    }

    class Failure {

        private final String message;

        public Failure(String message) {
            this.message = message;
        }

        public static Failure of(String reason) {
            return new Failure(reason);
        }

        public String getMessage() {
            return message;
        }
    }

    class ValidatorFilter implements HttpFilter {

        private final Logger logger;
        private final Parameter<?>[] parameters;

        public ValidatorFilter(Parameter<?>[] parameters) {
            this.logger = LoggerFactory.getLogger(getClass());
            this.parameters = parameters;
        }

        @Override
        public @NotNull Logger getLogger() {
            return this.logger;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public @NotNull CompletableFuture<@Nullable Void> filter(@NotNull HttpRequest httpRequest) {
            Map<Parameter<?>, Failure[]> failureMap = new HashMap<>();

            for (Parameter parameter : parameters) {
                List<Failure> failures = new ArrayList<>();
                List<Validator> validators = parameter.validators();

                Object obj = null;
                try {
                    obj = parameter.fetch(httpRequest);
                } catch (ParsingException e) {
                    failures.add(e.getFailure());
                }

                if (obj == null && parameter.required()) {
                    failures.add(Failure.of("Value not provided"));
                    continue;
                }

                if (validators != null) {
                    for (Validator validator : validators) {
                        Failure failure = validator.test(obj);
                        if (failure != null) {
                            failures.add(failure);
                        }
                    }
                }

                if (!failures.isEmpty()) {
                    failureMap.put(parameter, failures.toArray(Failure[]::new));
                }
            }

            if (failureMap.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            return CompletableFuture.failedFuture(new BadRequestException(failureMap));
        }
    }
}
