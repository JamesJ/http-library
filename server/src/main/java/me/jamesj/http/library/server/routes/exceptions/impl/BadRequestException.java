package me.jamesj.http.library.server.routes.exceptions.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.Source;
import me.jamesj.http.library.server.parameters.Validator;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.HttpException;

import java.util.HashMap;
import java.util.Map;

public class BadRequestException extends HttpException {

    private final Map<Parameter<?>, Validator.Failure[]> missing;

    public BadRequestException(String message) {
        super(400, "bad_request", message, null);
        this.missing = new HashMap<>();
    }

    public BadRequestException(Map<Parameter<?>, Validator.Failure[]> missing) {
        super(400, "bad_request", "Bad request", null);
        this.missing = missing;
    }

    @Override
    protected void populate(JsonObject jsonObject, HttpRequest httpRequest) {
        JsonArray array = new JsonArray();

        for (Parameter<?> parameter : this.missing.keySet()) {
            JsonArray elements = new JsonArray();
            for (Validator.Failure failure : this.missing.get(parameter)) {
                elements.add(toJson(failure));
            }
            JsonObject object = new JsonObject();
            JsonArray from = new JsonArray();
            parameter.sources().stream().map(Source::toJson).forEach(from::add);

            object.add("from", from);

            if (parameter.description() != null) {
                object.addProperty("description", parameter.description());
            }

            object.add("failures", elements);
            array.add(object);
        }

        if (array.size() > 0) {
            jsonObject.add("fields", array);
        }
    }

    private JsonObject toJson(Validator.Failure failure) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("reason", failure.getMessage());
        return jsonObject;
    }
}
