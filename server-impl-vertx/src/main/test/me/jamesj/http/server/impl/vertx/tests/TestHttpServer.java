package me.jamesj.http.server.impl.vertx.tests;

import me.jamesj.http.server.HttpConfiguration;
import me.jamesj.http.server.HttpServer;
import me.jamesj.http.server.impl.vertx.VertxHttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;

/**
 * Created by James on 10/12/2021
 */
public class TestHttpServer {
    
    private static HttpServer httpServer;
    private static HttpConfiguration httpConfiguration;
    
    @BeforeAll
    @Order(1)
    public static void createHttpConfiguration() {
        httpConfiguration = new HttpConfiguration()
            .port(8080);
    }
    
    @BeforeAll
    @Order(2)
    public static void createServer() {
        httpServer = new VertxHttpServer(httpConfiguration);
    }
    
    @BeforeAll
    @Order(3)
    public static void registerRoute() {
        httpServer.register(new TestHttpRoute());
    }
    
}
