package me.jamesj.http.library.parameters;

import java.util.*;

public class Builder<T> implements Parameter<T> {

    private final String name;
    private final Class<T> type;
    private boolean sensitive, required, file;
    private List<Source> sources;
    private String description;

    public Builder(String name, Class<T> type, boolean sensitive) {
        this.name = name;
        this.type = type;
        this.sensitive = sensitive;

        this.sources = new ArrayList<>(Collections.singleton(Source.BODY));
    }

    public Builder<T> query() {
        return sources(Source.QUERY);
    }

    public Builder<T> header() {
        return sources(Source.HEADER);
    }

    public Builder<T> path() {
        return sources(Source.PATH);
    }

    public Builder<T> body() {
        return sources(Source.BODY);
    }

    public Builder<T> sources(Source... sources) {
        this.sources = new ArrayList<>(Arrays.asList(sources));
        return this;
    }

    @Override
    public List<Source> sources() {
        return this.sources;
    }

    public Builder<T> sensitive() {
        return sensitive(true);
    }

    public Builder<T> sensitive(boolean sensitive) {
        this.sensitive = sensitive;
        return this;
    }

    public Builder<T> required() {
        return required(true);
    }

    public Builder<T> required(boolean required) {
        this.required = required;
        return this;
    }

    public Builder<T> file() {
        return this.file(true);
    }

    public Builder<T> file(boolean file) {
        this.file = file;
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isSensitive() {
        return sensitive;
    }

    public boolean isFile() {
        return file;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String description() {
        return this.description;
    }

    public Builder<T> describe(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Builder<?> that = (Builder<?>) o;
        return sensitive == that.sensitive && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(sources(), that.sources());
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, type, sensitive, sources());
    }
}
