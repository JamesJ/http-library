package me.jamesj.http.routes;

import me.jamesj.http.library.parameters.v2.Parameter;
import me.jamesj.http.util.HttpMethod;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public interface HttpRoute<T> {
    
    Logger getLogger();
    
    HttpMethod method();
    
    String path();
    
    CompletableFuture<T> handle(HttpRequest httpRequest);
    
    default Collection<Parameter<?>> parameters() {
        return Collections.emptyList();
    }
    
}
