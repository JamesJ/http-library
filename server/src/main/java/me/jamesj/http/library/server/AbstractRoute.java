package me.jamesj.http.library.server;

import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.Validator;
import me.jamesj.http.library.server.response.HttpResponse;
import me.jamesj.http.library.server.routes.HttpFilter;
import me.jamesj.http.library.server.routes.HttpRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractRoute<T extends HttpResponse> implements HttpRoute<T> {

    private final String path;
    private final HttpMethod method;
    private final Logger logger;

    private final List<Parameter<?>> parameters;
    private final List<HttpFilter> filters;

    public AbstractRoute(@NotNull String path, @NotNull HttpMethod method, @Nullable Parameter<?>... parameters) {
        this(path, method, Collections.emptyList(), parameters);
    }

    public AbstractRoute(@NotNull String path, @NotNull HttpMethod method, List<HttpFilter> filters, @Nullable Parameter<?>... parameters) {
        this.path = path;
        this.method = method;
        this.logger = LoggerFactory.getLogger(getClass());

        this.filters = new ArrayList<>(filters);

        if (parameters == null) {
            this.parameters = Collections.emptyList();
        } else {
            this.parameters = Arrays.asList(parameters);
        }

        if (parameters != null && parameters.length > 0) {
            this.filters.add(new Validator.ValidatorFilter(parameters));
        }
    }

    protected void filters(@NotNull HttpFilter... filters) {
        List<HttpFilter> httpFilters = new ArrayList<>(Arrays.asList(filters));

        this.filters.addAll(httpFilters);
    }

    public List<HttpFilter> filters() {
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
