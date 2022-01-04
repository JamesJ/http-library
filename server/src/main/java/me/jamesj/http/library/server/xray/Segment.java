package me.jamesj.http.library.server.xray;

public interface Segment {

    void addException(Throwable throwable);

    void end();

}
