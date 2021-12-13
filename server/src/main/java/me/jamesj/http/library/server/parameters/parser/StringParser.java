package me.jamesj.http.library.server.parameters.parser;

import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface StringParser<T> extends Parser<T> {

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
