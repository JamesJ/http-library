package me.jamesj.http.library.server.body.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.parameters.Source;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonBody implements Body {

    private static Gson GSON = new GsonBuilder().create();

    private final JsonObject jsonObject;

    public JsonBody(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public Source.Result get(String key) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (jsonElement.isJsonArray()) {
            List<String> list = new ArrayList<>();
            JsonArray jsonElements = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonElements) {
                list.add(element.getAsString());
            }
            return Source.Result.of(list);
        }
        if (jsonElement.isJsonObject()) {
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = GSON.fromJson(jsonElement, type);
            return Source.Result.of(map);
        }
        return Source.Result.of(jsonElement.getAsJsonPrimitive().getAsString());
    }

    @Override
    public int length() {
        return jsonObject.keySet().size();
    }

    public static class JsonBodyReader implements BodyReader {

        public static Gson GSON = new GsonBuilder().create();

        @Override
        public Body read(String body, Charset charset) {
            JsonObject jsonObject = GSON.fromJson(body, JsonObject.class);
            return new JsonBody(jsonObject);
        }
    }

}
