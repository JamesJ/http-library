package me.jamesj.http.library.server.routes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

public interface HttpFilter {

    @NotNull
    Logger getLogger();

    /**
     * @param httpRequest the request to filter
     * @return a CompletableFuture, returning null if the request should proceed, or an exception if it shouldnt - the exception will be parsed and presented to the end user
     */
    @NotNull
    CompletableFuture<@Nullable Void> filter(@NotNull HttpRequest httpRequest);

}
