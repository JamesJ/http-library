package me.jamesj.http.library.parameters;

public class ParameterValue<T> {

    private final Parameter<T> parameter;
    private final T t;

    public ParameterValue(Parameter<T> parameter, T t) {
        this.parameter = parameter;
        this.t = t;
    }

    public Parameter<T> getParameter() {
        return parameter;
    }

    public T getValue() {
        return t;
    }
}
