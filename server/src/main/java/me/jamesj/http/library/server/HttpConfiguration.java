package me.jamesj.http.library.server;

import com.github.shamil.Xid;

import java.util.function.Supplier;

public class HttpConfiguration {
    private SecureConfiguration secureConfiguration;
    private int port;
    private Supplier<String> requestIdGenerator;

    public HttpConfiguration() {
        this.port = 8080;
        this.requestIdGenerator = () -> "req_" + Xid.string();
    }

    public HttpConfiguration port(int port) {
        this.port = port;
        return this;
    }

    public HttpConfiguration requestIdGenerator(Supplier<String> supplier) {
        this.requestIdGenerator = supplier;
        return this;
    }

    public SecureConfiguration getSecureConfiguration() {
        return secureConfiguration;
    }

    public SecureConfiguration secure() {
        this.secureConfiguration = new SecureConfiguration(this);
        return this.secureConfiguration;
    }

    public int getPort() {
        return port;
    }

    public Supplier<String> getRequestIdGenerator() {
        return requestIdGenerator;
    }

    public static class SecureConfiguration {

        private final HttpConfiguration root;
        private String path, password;

        public SecureConfiguration(HttpConfiguration root) {
            this.root = root;
        }

        public SecureConfiguration password(String password) {
            this.password = password;
            return this;
        }

        public SecureConfiguration path(String path) {
            this.path = path;
            return this;
        }

        public String getPath() {
            return path;
        }

        public String getPassword() {
            return password;
        }

        public HttpConfiguration done() {
            return root;
        }

    }

}
