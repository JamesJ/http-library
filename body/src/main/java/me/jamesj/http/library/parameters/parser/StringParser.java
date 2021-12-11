package me.jamesj.http.library.parameters.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface StringParser<T> extends Parser<T> {
    
    @Nullable
    T parse(@NotNull String str);
    
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
}
