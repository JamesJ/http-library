package me.jamesj.http.library.body;

import me.jamesj.http.library.parameters.Parameter;

public interface Body {

    <T> T get(Parameter<T> key);

    int length();


}
