package me.jamesj.http.library.server.routes;

import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.Parameter;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public interface HttpRoute<T> {

    Logger getLogger();

    HttpMethod method();

    String path();

    CompletableFuture<T> handle(HttpRequest httpRequest) throws ParsingException;

    default Collection<Parameter<?>> parameters() {
        return Collections.emptyList();
    }

}
