package me.jamesj.http.library.server.telemetry;

import com.github.shamil.Xid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Telemetry {

    String id();

    long start();

    List<Operation> operations();

    void jinx(String key);

    class TelemetryImpl implements Telemetry {
        private final String id;
        private final long startTime;
        private final List<Operation> operations;

        public TelemetryImpl() {
            this.id = "tel_" + Xid.string();
            this.startTime = System.nanoTime();
            this.operations = new ArrayList<>();
        }

        @Override
        public String id() {
            return this.id;
        }

        @Override
        public long start() {
            return startTime;
        }

        @Override
        public List<Operation> operations() {
            return Collections.unmodifiableList(operations);
        }

        @Override
        public void jinx(String key) {
            this.operations.add(new Operation.OperationImpl(key, System.nanoTime()));
        }
    }

}
