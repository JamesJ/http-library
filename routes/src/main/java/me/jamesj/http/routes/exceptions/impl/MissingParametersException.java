package me.jamesj.http.routes.exceptions.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jamesj.http.library.parameters.Parameter;
import me.jamesj.http.library.parameters.validation.Failure;
import me.jamesj.http.routes.exceptions.HttpException;
import me.jamesj.http.routes.requests.HttpRequest;
import me.jamesj.http.util.JsonArrayCollector;

import java.util.Map;

public class MissingParametersException extends HttpException {

    private final Map<Parameter<?>, Failure> missing;

    public MissingParametersException(Map<Parameter<?>, Failure> missing) {
        super(400, "bad_request", "Bad request", null);
        this.missing = missing;
    }

    @Override
    protected void populate(JsonObject jsonObject, HttpRequest httpRequest) {
        JsonArray array = new JsonArray();

        for (Parameter<?> parameter : this.missing.keySet()) {
            array.add(toJson(parameter, this.missing.get(parameter)));
        }

        jsonObject.add("fields", array);
    }

    private JsonObject toJson(Parameter<?> parameter, Failure failure) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", parameter.name());

        jsonObject.add("in", parameter.sources().stream().map(source -> source.name().toLowerCase()).collect(new JsonArrayCollector()));
        if (parameter.description() != null) {
            jsonObject.addProperty("description", parameter.description());
        }
        jsonObject.addProperty("reason", failure.getMessage());
        return jsonObject;
    }
}
