package me.jamesj.http.library.server.body.impl;

import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import org.jetbrains.annotations.NotNull;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;

public class FormDataBody extends AbstractRequestBody {

    public FormDataBody(Map<String, Object> map) {
        super(map);
    }

    public static class FormDataReader implements BodyReader {

        @Override
        public Body read(String body, Charset charset) {
            String[] parts = body.split("&");

            Map<String, Object> map = new HashMap<>();

            for (String part : parts) {
                if (part.length() == 0) {
                    continue;
                }
                int pos = part.indexOf("=");

                if (pos == -1) {
                    // strict no value handling
                    continue;
                }

                String key, value;

                if (part.contains("[]=")) {
                    // array parsing

                    key = part.substring(0, part.indexOf("["));
                    value = part.substring(pos + 1);

                    List<String> array = explodeArray(value, charset);
                    addToMap(key, map, array, charset);
                } else if (part.contains("]=")) {
                    // map parsing

                    key = part.substring(0, pos);
                    String subKey = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                    key = part.substring(0, key.indexOf("["));
                    value = part.substring(pos + 1);

                    addToMap(key, map, new AbstractMap.SimpleEntry<>(subKey, value), charset);
                } else {
                    // ordinary value parsing
                    key = part.substring(0, pos);
                    value = part.substring(pos + 1);

                    addToMap(key, map, value, charset);
                }


            }

            return new FormDataBody(map);
        }

        private void addToMap(@NotNull String key, @NotNull Map<String, Object> map, @NotNull String value, Charset charset) {
            map.put(key, decode(value, charset));
        }

        private void addToMap(@NotNull String key, @NotNull Map<String, Object> map, @NotNull Map.Entry<String, String> entry, Charset charset) {
            Object obj = map.get(key);
            if (!(obj instanceof Map)) {
                if (obj == null) {
                    obj = new HashMap<>();
                } else {
                    throw new IllegalStateException("Incompatible mix match of types (is " + (obj.getClass()) + ")");
                }
            }
            Map<String, String> current = (Map<String, String>) obj;
            current.put(entry.getKey(), decode(entry.getValue(), charset));

            map.put(key, current);
        }

        private void addToMap(@NotNull String key, @NotNull Map<String, Object> map, @NotNull List<String> list, @NotNull Charset charset) {
            Object obj = map.get(key);
            if (obj instanceof Map) {
                throw new IllegalStateException("Incompatible mix match of types (is " + (obj.getClass()) + ")");
            }

            List<String> current;
            if (obj instanceof List) {
                current = (List<String>) obj;
            } else {
                current = new ArrayList<>();
                if (obj != null) {
                    current.add(decode((String) obj, charset));
                }
            }

            current.addAll(list);
            map.put(key, current);
        }

        private List<String> explodeArray(String value, Charset charset) {
            List<String> list = new ArrayList<>();
            if (value.contains(",")) {
                String[] split = value.split(",");
                for (String s : split) {
                    list.add(decode(s, charset));
                }
            } else {
                list.add(decode(value, charset));
            }
            return list;
        }

        private String decode(String str, Charset charset) {
            try {
                return URLDecoder.decode(str, charset);
            } catch (Throwable throwable) {
                return str;
            }
        }
    }
}
