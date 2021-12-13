package me.jamesj.http.library.server;

import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.Validator;
import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRoute;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractRoute<T extends HttpResponse<?>> implements HttpRoute<T> {

    private final String path;
    private final HttpMethod method;
    private final Logger logger;

    private final List<Parameter<?>> parameters;
    private final List<HttpFilter> filters;

    public AbstractRoute(@NotNull String path, @NotNull HttpMethod method) {
        this.path = path;
        this.method = method;
        this.logger = LoggerFactory.getLogger(getClass());

        this.parameters = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    protected void filters(@NotNull HttpFilter... filters) {
        List<HttpFilter> httpFilters = new ArrayList<>(Arrays.asList(filters));

        this.filters.addAll(httpFilters);
    }

    protected void validate() {
        filters(new Validator.ValidatorFilter(parameters.toArray(Parameter[]::new)));
    }

    protected void parameter(@NotNull Parameter<?>... parameter) {
        this.parameters.addAll(Arrays.asList(parameter));
    }

    public Collection<HttpFilter> filters() {
        return Collections.unmodifiableList(this.filters);
    }

    @Override
    public Collection<Parameter<?>> parameters() {
        return this.parameters;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public HttpMethod method() {
        return method;
    }

    @Override
    public String path() {
        return this.path;
    }
}
