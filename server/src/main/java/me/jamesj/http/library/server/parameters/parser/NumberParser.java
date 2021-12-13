package me.jamesj.http.library.server.parameters.parser;

import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.Validator;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface NumberParser<T extends Number> extends StringParser<T> {

    @Nullable
    T parse(@NotNull Number number);

    @Override
    default @Nullable T parse(@NotNull String str) {
        return parse(NumberUtils.createNumber(str));
    }

    @Override
    default @Nullable T parse(@NotNull Object data, Map<String, String> metadata) {
        String string;
        if (data instanceof String) {
            string = (String) data;
        } else if (data instanceof byte[]) {
            string = new String((byte[]) data, StandardCharsets.UTF_8);
        } else {
            string = data.toString();
        }
        return parse(string);
    }

    @Override
    default boolean accepts(Object data) throws ParsingException {
        if (data == null) {
            throw new ParsingException(Validator.Failure.of("Value is null"));
        }
        String str;
        if (data instanceof String) {
            str = (String) data;
        } else {
            str = data.toString();
        }
        if (NumberUtils.isCreatable(str)) {
            return true;
        }
        throw new ParsingException(Validator.Failure.of("Value is not a valid number"));
    }
}
