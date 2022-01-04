package me.jamesj.http.library.server.xray;

public interface Xray {

    Segment startSegment(String name);

    void endSegment();

}
