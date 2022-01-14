package me.jamesj.http.library.server.impl.vertx.tests;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.HttpConfiguration;
import me.jamesj.http.library.server.HttpMethod;
import me.jamesj.http.library.server.impl.vertx.VertxHttpServer;
import me.jamesj.http.library.server.impl.vertx.tests.util.RequesterUtils;
import me.jamesj.http.library.server.parameters.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by James on 10/12/2021
 */
public class TestHttpServer {

    private static final Logger logger = LoggerFactory.getLogger(TestHttpServer.class);

    public TestHttpServer() {
        HttpConfiguration httpConfiguration = new HttpConfiguration()
                .port(8089);
        VertxHttpServer httpServer = new VertxHttpServer(httpConfiguration);

        httpServer.register(new TestHttpRoute());
        httpServer.register(new TestHttpRouteWithParameters());
        httpServer.start();

        logger.info("Server started");
    }


    @Test
    @Order(1)
    public void testNotFoundPage() throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = RequesterUtils.request(HttpMethod.GET, "/", new HashMap<>(), new HashMap<>(), null);
        assertAll(() -> {
            assertEquals(404, httpResponse.statusCode());
            assertEquals(MediaType.JSON_UTF_8.toString(), httpResponse.headers().firstValue("Content-Type").orElse(null));
            assertEquals("{\"error_code\":\"route_not_found\",\"message\":\"Route not found\",\"error\":true}", httpResponse.body());
        });
    }

    @Test
    @Order(2)
    public void testSampleResponse() throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = RequesterUtils.request(HttpMethod.GET, "/test-path", new HashMap<>(), new HashMap<>(), null);
        assertEquals(200, httpResponse.statusCode());
        assertEquals(MediaType.ANY_TYPE.toString(), httpResponse.headers().firstValue("Content-Type").orElse(null));
        assertEquals(TestHttpRoute.BODY, httpResponse.body());
    }

    @Test
    @Order(3)
    public void testWhenShouldHaveParams() throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = RequesterUtils.request(HttpMethod.POST, "/test-path-with-parameters", new HashMap<>(), new HashMap<>(), null);
        assertEquals(400, httpResponse.statusCode());
    }


}
