package me.jamesj.http.library.server.parameters.parser;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.parameters.files.File;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Parser<T> {

    @NotNull
    static StringParser<String> asString() {
        return str -> str;
    }

    @NotNull
    static StringParser<Number> asNumber() {
        return NumberUtils::createNumber;
    }

    @NotNull
    static StringParser<Integer> asInteger() {
        return NumberUtils::createInteger;
    }

    @NotNull
    static StringParser<Double> asDouble() {
        return NumberUtils::createDouble;
    }

    @NotNull
    static StringParser<Long> asLong() {
        return NumberUtils::createLong;
    }

    @NotNull
    static StringParser<Float> asFloat() {
        return NumberUtils::createFloat;
    }

    @NotNull
    static StringParser<Byte> asByte() {
        return NumberUtils::toByte;
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

}
