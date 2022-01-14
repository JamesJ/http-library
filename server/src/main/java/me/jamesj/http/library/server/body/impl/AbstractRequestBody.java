package me.jamesj.http.library.server.body.impl;

import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.parameters.Source;

import java.util.Map;

public abstract class AbstractRequestBody implements Body {

    private final Map<String, Object> map;

    protected AbstractRequestBody(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public Source.Result get(String key) {
        Object result = this.map.get(key);
        if (result == null) {
            return null;
        }
        return Source.Result.of(result);
    }

    @Override
    public int length() {
        return this.map.size();
    }
}
