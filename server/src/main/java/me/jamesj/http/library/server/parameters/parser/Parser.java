package me.jamesj.http.library.server.parameters.parser;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.Validator;
import me.jamesj.http.library.server.parameters.files.File;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Parser<T> {

    @NotNull
    static StringParser<String> asString() {
        return (parameter, str) -> str;
    }

    static <E extends Enum<E>> StringParser<E> asEnum(Class<E> clazz) {
        return (parameter, str) -> {
            if (str == null) {
                throw new ParsingException(parameter, Validator.Failure.of("No value provided"));
            }
            try {
                return Enum.valueOf(clazz, str.toUpperCase());
            } catch (IllegalArgumentException e) {
                List<String> options = new ArrayList<>();
                for (E enumConstant : clazz.getEnumConstants()) {
                    options.add("\"" + enumConstant.name().toLowerCase() + "\"");
                }
                throw new ParsingException(parameter, Validator.Failure.of("Value \"" + str + "\" is not valid, supported values: [" + String.join(",", options) + "]"));
            }
        };
    }

    @NotNull
    static NumberParser<Number> asNumber() {
        return (parameter, number) -> number;
    }

    @NotNull
    static NumberParser<Integer> asInteger() {
        return (parameter, number) -> number.intValue();
    }

    @NotNull
    static NumberParser<Double> asDouble() {
        return (parameter, number) -> number.doubleValue();
    }

    @NotNull
    static NumberParser<Long> asLong() {
        return (parameter, number) -> number.longValue();
    }

    @NotNull
    static NumberParser<Float> asFloat() {
        return (parameter, number) -> number.floatValue();
    }

    @NotNull
    static NumberParser<Byte> asByte() {
        return (parameter, number) -> number.byteValue();
    }

    static Parser<Map<String, String>> asMap() {
        return (parameter, data, metadata) -> {
            if (data instanceof Map) {
                return (Map<String, String>) data;
            }
            throw new ParsingException(parameter, Validator.Failure.of("Cannot parse as map"));
        };
    }

    @NotNull
    static StringParser<Boolean> asBoolean() {
        return (parameter, str) -> {
            if (str.equalsIgnoreCase("yes")
                    || str.equalsIgnoreCase("y")
                    || str.equalsIgnoreCase("t")
                    || str.equalsIgnoreCase("1")) {
                return true;
            }
            return asStrictBoolean().parse(parameter, str);
        };
    }

    @NotNull
    static StringParser<Boolean> asStrictBoolean() {
        return (parameter, str) -> str.equalsIgnoreCase("true");
    }

    @NotNull
    static Parser<File> asFile() {
        return (parameter, data, metadata) -> {
            if (data instanceof byte[]) {
                MediaType mediaType = MediaType.parse(metadata.get("content-type"));
                return new File.FileImpl(metadata.get("name"), mediaType, (byte[]) data);
            }
            return null;
        };
    }

    @Nullable T parse(Parameter<T> parameter, @NotNull Object data, Map<String, String> metadata);

    interface NumberParser<T extends Number> extends StringParser<T> {

        @Nullable
        T parse(Parameter<T> parameter, @NotNull Number number) throws ParsingException;

        @Override
        default @Nullable T parse(@NotNull Parameter<T> parameter, @NotNull String str) throws ParsingException {
            return parse(parameter, NumberUtils.createNumber(str));
        }

        @Override
        default @Nullable T parse(@NotNull Parameter<T> parameter, @NotNull Object data, Map<String, String> metadata) {
            String string;
            if (data instanceof String) {
                string = (String) data;
            } else if (data instanceof byte[]) {
                string = new String((byte[]) data, StandardCharsets.UTF_8);
            } else {
                string = data.toString();
            }


            if (NumberUtils.isCreatable(string)) {
                return parse(parameter, string);
            }
            throw new ParsingException(parameter, Validator.Failure.of("Value is not a valid number"));
        }
    }

    interface StringParser<T> extends Parser<T> {

        @Nullable
        T parse(@NotNull Parameter<T> parameter, @NotNull String str) throws ParsingException;

        @Override
        default @Nullable T parse(@NotNull Parameter<T> parameter, @NotNull Object data, Map<String, String> metadata) throws ParsingException {
            String string;
            if (data instanceof String) {
                string = (String) data;
            } else if (data instanceof byte[]) {
                string = new String((byte[]) data, StandardCharsets.UTF_8);
            } else {
                string = data.toString();
            }
            return parse(parameter, string);
        }
    }


}
