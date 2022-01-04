package me.jamesj.http.library.server.impl.lambda;

import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.entities.Segment;
import me.jamesj.http.library.server.Xray;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AwsXray implements Xray {

    private final AWSXRayRecorder recorder;
    private final Map<String, Segment> segmentMap;

    public AwsXray() {
        recorder = AWSXRayRecorderBuilder.defaultRecorder();
        this.segmentMap = new ConcurrentHashMap<>();
    }

    @Override
    public void startSegment(String name) {
        Segment segment = recorder.beginSegment(name);
        segmentMap.put(name, segment);
    }

    @Override
    public void endSegment(String name) {
        Segment segment = segmentMap.get(name);
        if (segment == null) {
            return;
        }
        segment.end();
        segmentMap.remove(name);
    }
}
