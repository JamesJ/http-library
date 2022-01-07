package me.jamesj.http.library.server.routes.exceptions.impl;

import com.google.gson.JsonObject;
import me.jamesj.http.library.server.routes.HttpRequest;
import me.jamesj.http.library.server.routes.exceptions.HttpException;

public class RouteNotFoundException extends HttpException {
    public RouteNotFoundException() {
        super(404, "route_not_found", "Route not found", null);
    }

    @Override
    protected void populate(JsonObject jsonObject, HttpRequest httpRequest) {

    }
}
