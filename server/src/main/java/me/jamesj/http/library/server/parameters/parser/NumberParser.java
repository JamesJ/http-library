package me.jamesj.http.library.server.parameters.parser;

import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.Validator;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface NumberParser<T extends Number> extends StringParser<T> {

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
