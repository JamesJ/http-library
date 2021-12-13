package me.jamesj.http.library.server.parameters.parser;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.files.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Parser<T> {

    @NotNull
    static StringParser<String> asString() {
        return str -> str;
    }

    @NotNull
    static NumberParser<Number> asNumber() {
        return number -> number;
    }

    @NotNull
    static NumberParser<Integer> asInteger() {
        return Number::intValue;
    }

    @NotNull
    static NumberParser<Double> asDouble() {
        return Number::doubleValue;
    }

    @NotNull
    static NumberParser<Long> asLong() {
        return Number::longValue;
    }

    @NotNull
    static NumberParser<Float> asFloat() {
        return Number::floatValue;
    }

    @NotNull
    static NumberParser<Byte> asByte() {
        return Number::byteValue;
    }

    @NotNull
    static StringParser<Boolean> asBoolean() {
        return (str) -> {
            if (str.equalsIgnoreCase("yes")
                    || str.equalsIgnoreCase("y")
                    || str.equalsIgnoreCase("t")
                    || str.equalsIgnoreCase("1")) {
                return true;
            }
            return asStrictBoolean().parse(str);
        };
    }

    @NotNull
    static StringParser<Boolean> asStrictBoolean() {
        return str -> str.equalsIgnoreCase("true");
    }

    @NotNull
    static Parser<File> asFile() {
        return (data, metadata) -> {
            if (data instanceof byte[]) {
                MediaType mediaType = MediaType.parse(metadata.get("content-type"));
                return new File.FileImpl(metadata.get("name"), mediaType, (byte[]) data);
            }
            return null;
        };
    }

    @Nullable T parse(@NotNull Object data, Map<String, String> metadata);

    default boolean accepts(Object data) throws ParsingException {
        return true;
    }

}
