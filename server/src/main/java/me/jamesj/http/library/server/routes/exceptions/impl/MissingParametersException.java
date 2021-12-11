package me.jamesj.http.library.server.routes.exceptions.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jamesj.http.library.server.parameters.v2.Parameter;
import me.jamesj.http.library.server.parameters.v2.Validator;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.HttpException;
import me.jamesj.http.library.server.util.JsonArrayCollector;

import java.util.Map;

public class MissingParametersException extends HttpException {
    
    private final Map<Parameter<?>, Validator.Failure> missing;
    
    public MissingParametersException(Map<Parameter<?>, Validator.Failure> missing) {
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
    
    private JsonObject toJson(Parameter<?> parameter, Validator.Failure failure) {
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
