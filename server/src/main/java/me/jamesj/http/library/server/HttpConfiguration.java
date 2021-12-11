package me.jamesj.http.library.server;

import com.github.shamil.Xid;

import java.util.function.Supplier;

public class HttpConfiguration {
    
    //todo: secure options
    
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
    
    public int getPort() {
        return port;
    }
    
    public Supplier<String> getRequestIdGenerator() {
        return requestIdGenerator;
    }
    
}
