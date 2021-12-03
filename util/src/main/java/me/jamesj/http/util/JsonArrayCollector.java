package me.jamesj.http.util;


import com.google.gson.JsonArray;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

public class JsonArrayCollector implements Collector<String, List<String>, JsonArray> {

    @Override
    public Supplier<List<String>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<String>, String> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<String>> combiner() {
        return (strings, strings2) -> {
            strings.addAll(strings2);
            return strings;
        };
    }

    @Override
    public Function<List<String>, JsonArray> finisher() {
        return strings -> {
            JsonArray jsonArray = new JsonArray();
            for (String string : strings) {
                jsonArray.add(string);
            }
            return jsonArray;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}