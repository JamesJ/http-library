package me.jamesj.http.library.server.response;


import com.google.common.net.MediaType;
import me.jamesj.http.library.server.routes.HttpRequest;

@SuppressWarnings("UnstableApiUsage")
public interface HttpResponse<T> {

    T build(HttpRequest request);

    int getStatusCode();

    MediaType getMediaType();

    default boolean isBase64Encoded() {
        return false;
    }

}
