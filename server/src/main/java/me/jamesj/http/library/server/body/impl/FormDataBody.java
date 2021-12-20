package me.jamesj.http.library.server.body.impl;

import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.parameters.Source;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FormDataBody implements Body {

    @Override
    public Source.Result get(String key) {
        return null;
    }

    @Override
    public int length() {
        return 0;
    }

    public static class FormDataReader implements BodyReader {

        private static final String KEY_ARRAY_REGEX = "";
        private static final String KEY_MAP_REGEX = "";

        @Override
        public Body read(String body, boolean isBase64) throws BodyParsingException {
            if (isBase64) {
                body = Arrays.toString(Base64.getDecoder().decode(body));
            }
            String[] parts = body.split("&");

            Map<String, Object> map = new HashMap<>();
            for (String part : parts) {
                String[] split = part.split("=");
                if (split.length == 0) {
                    continue;
                }
                String key = split[0];
                String value = null;
                if (split.length == 2) {
                    value = split[1];
                }
                if (StringUtils.isAlphanumericSpace(key)) {

                } else if (key.matches(KEY_ARRAY_REGEX)) {

                }
            }
            return null;
        }

        private void addToMap(@NotNull Map<String, Object> map, @NotNull String key, @Nullable String subKey, @Nullable String value) {
            Object current = map.get(key);
            if (current == null) {
                map.put(key, value);
                return;
            }
            if (current instanceof Map) {
                if (subKey != null) {
                    Map<String, String> submap = (Map<String, String>) current;
                    submap.put(subKey, value);
                    map.put(key, submap);
                }
            } else {
                List<String> list;
                if (current instanceof List) {
                    list = (List<String>) current;
                } else {
                    list = new ArrayList<>();
                    list.add((String) current);
                }
                list.add(value);
                map.put(key, value);
            }
        }
    }
}
