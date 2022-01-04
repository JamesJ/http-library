package me.jamesj.http.library.server.impl.lambda;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import me.jamesj.http.library.server.xray.Segment;
import me.jamesj.http.library.server.xray.Xray;

public class AwsXray implements Xray {

    @Override
    public AwsXraySegment startSegment(String name) {
        Subsegment subsegment = AWSXRay.beginSubsegment(name);

        return new AwsXraySegment(subsegment);
    }

    @Override
    public void endSegment() {
        AWSXRay.endSubsegment();
    }

    public static class AwsXraySegment implements Segment {

        private final Subsegment subsegment;

        AwsXraySegment(Subsegment subsegment) {
            this.subsegment = subsegment;
        }

        @Override
        public void addException(Throwable throwable) {
            subsegment.addException(throwable);
        }

        @Override
        public void end() {
            this.subsegment.end();
        }
    }
}
