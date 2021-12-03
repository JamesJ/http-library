package me.jamesj.http.library.parameters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParameterMap {

    private final List<ParameterValue<?>> list;

    public ParameterMap(@NotNull List<ParameterValue<?>> list) {
        this.list = list;
    }

    @Nullable
    public <T> T get(@NotNull Parameter<T> parameter) {
        for (ParameterValue<?> parameterValue : list) {
            if (parameter == parameterValue.getParameter()) {
                return (T) parameterValue.getValue();
            }
        }
        return null;
    }
}
