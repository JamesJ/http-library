package me.jamesj.http.library.server;

public interface Xray {

    void startSegment(String name);

    void endSegment(String name);

}
