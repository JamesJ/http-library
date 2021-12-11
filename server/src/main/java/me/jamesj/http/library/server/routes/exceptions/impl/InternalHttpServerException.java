package me.jamesj.http.library.server.routes.exceptions.impl;

import com.github.shamil.Xid;
import com.google.gson.JsonObject;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.HttpException;

import java.util.function.Supplier;

public class InternalHttpServerException extends HttpException {
    
    public static Supplier<String> ERROR_ID_GENERATOR = Xid::string;
    
    
    private final String id;
    
    public InternalHttpServerException(Throwable throwable) {
        this("Internal server error", throwable);
    }
    
    public InternalHttpServerException(String message, Throwable throwable) {
        this("server_error", message, throwable);
    }
    
    public InternalHttpServerException(String errorCode, String message, Throwable throwable) {
        super(500, errorCode, message, throwable);
        
        this.id = ERROR_ID_GENERATOR.get();
    }
    
    @Override
    protected void populate(JsonObject jsonObject, HttpRequest httpRequest) {
        jsonObject.addProperty("error_id", id);
    }
    
    public String getId() {
        return id;
    }
}
