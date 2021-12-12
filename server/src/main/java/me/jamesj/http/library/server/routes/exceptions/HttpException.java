package me.jamesj.http.library.server.routes.exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.response.json.JsonResponse;

public abstract class HttpException extends Exception implements JsonResponse {
    
    private final int statusCode;
    private final String errorCode;
    private final String message;
    
    public HttpException(int statusCode, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
    
    @Override
    public JsonElement build(HttpRequest request) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("error_code", errorCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("error", true);
        
        populate(jsonObject, request);
        
        return jsonObject;
    }
    
    protected abstract void populate(JsonObject jsonObject, HttpRequest httpRequest);
    
    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
