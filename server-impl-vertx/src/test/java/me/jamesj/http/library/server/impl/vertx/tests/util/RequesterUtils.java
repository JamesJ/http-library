package me.jamesj.http.library.server.impl.vertx.tests.util;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import me.jamesj.http.library.server.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequesterUtils {

    public static HttpClient httpClient = HttpClient.newHttpClient();

    public static HttpResponse<String> request(HttpMethod method, String uri, Map<String, String> headers, Map<String, String> query, String body) throws IOException, InterruptedException {
        String queryString = "";

        if (query != null && !query.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("?");
            query.forEach((s, o) -> {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(s).append("=").append(URLEncoder.encode(o, StandardCharsets.UTF_8));
            });

            queryString = stringBuilder.toString();
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089" + uri + queryString));

        headers.forEach(builder::header);

        switch (method) {
            case GET: {
                builder.GET();
                break;
            }
            case POST: {
                builder.POST(bodyPublisher(builder, body));
                break;
            }
            case DELETE: {
                builder.DELETE();
                break;
            }
            case PUT: {
                builder.PUT(bodyPublisher(builder, body));
                break;
            }
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static HttpRequest.BodyPublisher bodyPublisher(HttpRequest.Builder builder, String body) {
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.FORM_DATA.toString());
        if (body == null) {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.ofString(body);
    }

}
