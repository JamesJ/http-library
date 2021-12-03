package me.jamesj.http.library.parameters;

import java.util.List;

public interface Parameter<T> {

    String name();

    boolean isSensitive();

    boolean isRequired();

    String description();

    List<Source> sources();

    enum Source {
        PATH,
        BODY,
        HEADER,
        QUERY;
    }

}
