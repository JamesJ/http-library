package me.jamesj.http.server;

import java.util.function.Supplier;

public class HttpConfiguration {

    private int port;

    private Supplier<String> requestIdGenerator;

}
