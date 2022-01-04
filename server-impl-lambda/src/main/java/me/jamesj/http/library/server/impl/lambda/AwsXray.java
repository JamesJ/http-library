package me.jamesj.http.library.server.impl.lambda;

import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import me.jamesj.http.library.server.Xray;

public class AwsXray implements Xray {

    private final AWSXRayRecorder recorder;

    public AwsXray() {
        recorder = AWSXRayRecorderBuilder.defaultRecorder();
    }

    @Override
    public void startSegment(String name) {
        recorder.beginSegment(name);
    }

    @Override
    public void endSegment() {
        recorder.endSegment();
    }
}
