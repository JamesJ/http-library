package me.jamesj.http.routes;

import me.jamesj.http.library.parameters.Parameter;
import me.jamesj.http.routes.requests.HttpRequest;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

public interface HttpRoute<T> {

    Logger getLogger();

    HttpMethod method();

    String path();

    T handle(HttpRequest httpRequest);

    default List<Parameter<?>> parameters() {
        return Collections.emptyList();
    }

}
