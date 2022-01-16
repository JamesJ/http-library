package me.jamesj.http.library.server.parameters;

import me.jamesj.http.library.server.body.exceptions.impl.ParsingException;
import me.jamesj.http.library.server.parameters.files.File;
import me.jamesj.http.library.server.parameters.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface Parameter<T> {

    static Builder<String> string() {
        return new Builder<>(Parser.asString());
    }
    static Builder<Integer> integer() {
        return new Builder<>(Parser.asInteger());
    }
    static Builder<Boolean> bool() {
        return new Builder<>(Parser.asBoolean());
    }
    static Builder<File> file() {
        return new Builder<>(Parser.asFile());
    }
    static Builder<Map<String, String>> map() {
        return new Builder<>(Parser.asMap());
    }
    static <E extends Enum<E>> Builder<E> asEnum(Class<E> clazz) {
        return new Builder<>(Parser.asEnum(clazz));
    }

    List<Validator<T>> validators();

    /**
     * @return used to parse the incoming data
     * @see Parser
     */
    @NotNull
    Parser<T> parser();

    /**
     * @return if the parameter is sensitive, therefore filtered from any logging
     */
    boolean sensitive();

    /**
     * @return if the parameter is required
     */
    boolean required();

    @Nullable
    String description();

    @NotNull
    Collection<@NotNull Source> sources();

    @Nullable
    T fetch(@NotNull ParameterHolder request) throws ParsingException;

    class ParameterImpl<T> implements Parameter<T> {
        private final String description;
        private final Parser<T> parser;
        private final boolean sensitive, required;
        private final Collection<Source> sources;
        private final List<Validator<T>> validators;

        public ParameterImpl(String description, Parser<T> parser, boolean sensitive, boolean required, Collection<Source> sources, List<Validator<T>> validators) {
            this.description = description;
            this.parser = parser;
            this.sensitive = sensitive;
            this.required = required;
            this.sources = sources;
            this.validators = validators;
        }

        @Override
        public @NotNull Parser<T> parser() {
            return this.parser;
        }

        @Override
        public boolean sensitive() {
            return this.sensitive;
        }

        @Override
        public boolean required() {
            return this.required;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public @NotNull Collection<Source> sources() {
            return this.sources;
        }

        @Override
        public List<Validator<T>> validators() {
            return this.validators;
        }

        @Override
        public @Nullable T fetch(@NotNull ParameterHolder request) throws ParsingException {
            if (sources().isEmpty()) {
                return null;
            }

            for (Source source : sources()) {
                Source.Result result = source.collect(request);
                if (result != null) {
                    return handle(this, result);
                }
            }
            return null;
        }

        private T handle(Parameter<T> parameter, Source.Result result) throws ParsingException {
            return this.parser.parse(parameter, result.getData(), result.getMetadata());
        }

    }

    class Builder<T> {

        private final Collection<Source> sources;
        private final List<Validator<T>> validators;
        private final Parser<T> parser;
        private String description;
        private boolean required, sensitive;

        protected Builder(Parser<T> parser) {
            this.parser = parser;

            this.sources = new LinkedList<>();
            this.validators = new LinkedList<>();
        }

        @NotNull
        public Builder<T> source(@NotNull Source source) {
            this.sources.add(source);
            return this;
        }

        @NotNull
        public Builder<T> validator(@NotNull Validator<T> validator) {
            this.validators.add(validator);
            return this;
        }

        @NotNull
        public Builder<T> length(@Nullable Integer minimum, @Nullable Integer maximum) {
            if (maximum != null) {
                validator(Validator.max(maximum));
            }
            if (minimum != null) {
                validator(Validator.min(minimum));
            }
            return this;
        }

        @NotNull
        public Builder<T> header(@NotNull String name) {
            return this.source(Source.header(name));
        }

        @NotNull
        public Builder<T> query(@NotNull String name) {
            return this.source(Source.query(name));
        }

        @NotNull
        public Builder<T> path(@NotNull String name) {
            return this.source(Source.path(name));
        }

        @NotNull
        public Builder<T> form(@NotNull String name) {
            return this.source(Source.form(name));
        }

        @NotNull
        public Builder<T> description(@Nullable String description) {
            this.description = description;
            return this;
        }

        @NotNull
        public Builder<T> sensitive() {
            return this.sensitive(true);
        }

        @NotNull
        public Builder<T> sensitive(boolean sensitive) {
            this.sensitive = sensitive;
            return this;
        }

        @NotNull
        public Builder<T> required() {
            return this.required(true);
        }

        @NotNull
        public Builder<T> required(boolean required) {
            this.required = required;
            return this;
        }

        public Parameter<T> build() {
            return new ParameterImpl<T>(this.description, this.parser, this.sensitive, this.required, this.sources, this.validators);
        }
    }

}
