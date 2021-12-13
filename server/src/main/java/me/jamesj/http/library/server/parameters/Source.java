package me.jamesj.http.library.server.parameters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface Source {
    @NotNull
    static Header header(@NotNull String name) {
        return new Header(name);
    }

    @NotNull
    static Path path(@NotNull String name) {
        return new Path(name);
    }

    @NotNull
    static Query query(@NotNull String name) {
        return new Query(name);
    }

    @NotNull
    static Form form(@NotNull String name) {
        return new Form(name);
    }

    @NotNull
    String name();

    @NotNull
    String from();

    @Nullable
    Result collect(@NotNull ParameterHolder request);

    abstract class AbstractSource implements Source {
        private final String name, from;

        protected AbstractSource(@NotNull String name, @NotNull String from) {
            this.name = name;
            this.from = from;
        }

        @Override
        public @NotNull String from() {
            return this.from;
        }

        @Override
        public @NotNull String name() {
            return this.name;
        }
    }

    class Header extends AbstractSource {

        protected Header(String name) {
            super(name, "header");
        }

        @Override
        public Result collect(@NotNull ParameterHolder request) {
            String[] list = request.headers().get(this.name());
            if (list == null || list.length == 0) {
                return null;
            }

            return Result.of(list[0].getBytes(StandardCharsets.UTF_8));
        }
    }

    class Query extends AbstractSource {

        protected Query(String name) {
            super(name, "query");
        }

        @Override
        public Result collect(@NotNull ParameterHolder request) {
            String[] list = request.query().get(this.name());
            if (list == null || list.length == 0) {
                return null;
            }

            return Result.of(list[0].getBytes(StandardCharsets.UTF_8));
        }
    }

    class Path extends AbstractSource {

        protected Path(String name) {
            super(name, "path");
        }

        @Override
        public Result collect(@NotNull ParameterHolder request) {
            String result = request.pathParams().get(this.name());
            if (result == null) {
                return null;
            }
            return Result.of(result.getBytes(StandardCharsets.UTF_8));
        }
    }

    class Form extends AbstractSource {

        protected Form(String name) {
            super(name, "form");
        }

        @Override
        public Result collect(@NotNull ParameterHolder request) {
            return request.body().get(this.name());
        }
    }

    /**
     * needed to store both the bytes value of the parameter, and any associated metadata
     * metadata is normally empty/null, however for certain situations it may be populated
     * such as storing a file (for it's file name, content type, etc)
     *
     * @see me.jamesj.http.library.server.parameters.parser.Parser
     * @see me.jamesj.http.library.server.parameters.files.File
     */
    class Result {
        private final Object data;
        private final Map<String, String> metadata;

        public Result(Object data, Map<String, String> metadata) {
            this.data = data;
            this.metadata = metadata;
        }

        private static Result of(byte[] bytes) {
            return new Result(bytes, Map.of());
        }

        public Object getData() {
            return data;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "data=" + data +
                    ", metadata=" + metadata +
                    '}';
        }
    }
}
