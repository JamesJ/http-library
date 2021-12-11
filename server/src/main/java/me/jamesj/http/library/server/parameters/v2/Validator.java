package me.jamesj.http.library.server.parameters.v2;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.parameters.files.File;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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
}
