package me.jamesj.http.library.server;

public enum HttpMethod {

    GET(),
    POST(true),
    DELETE(true),
    PUT(true),
    OPTIONS(),
    CONNECT(),
    TRACE(),
    PATCH(true),
    HEAD();

    private final boolean supportsBody;

    HttpMethod() {
        this(false);
    }
    HttpMethod(boolean supportsBody) {
        this.supportsBody = supportsBody;
    }

    public boolean hasBodySupport() {
        return supportsBody;
    }
}
