package me.jamesj.http.library.server.response.json;

import com.google.common.net.MediaType;
import com.google.gson.JsonElement;
import me.jamesj.http.library.server.response.HttpResponse;

@SuppressWarnings("UnstableApiUsage")
public interface JsonResponse extends HttpResponse<JsonElement> {

    @Override
    default MediaType getMediaType() {
        return MediaType.JSON_UTF_8;
    }
}
