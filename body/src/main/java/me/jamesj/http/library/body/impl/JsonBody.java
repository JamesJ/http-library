package me.jamesj.http.library.body.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.jamesj.http.library.body.Body;
import me.jamesj.http.library.body.BodyReader;
import me.jamesj.http.library.body.exceptions.BodyParsingException;
import me.jamesj.http.library.parameters.Parameter;

import java.util.Base64;

public class JsonBody implements Body {

    private final JsonObject jsonObject;

    public JsonBody(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public <T> T get(Parameter<T> key) {
        return null;
    }

    @Override
    public int length() {
        return jsonObject.keySet().size();
    }

    public static class JsonBodyReader implements BodyReader {

        public static Gson GSON = new GsonBuilder().create();

        @Override
        public Body read(String body, boolean isBase64) throws BodyParsingException {
            if (isBase64) {
                body = new String(Base64.getDecoder().decode(body));
            }
            JsonObject jsonObject = GSON.fromJson(body, JsonObject.class);
            return new JsonBody(jsonObject);
        }
    }

}
