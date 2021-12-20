package me.jamesj.http.library.server.telemetry;

public interface Operation {

    String name();

    long timestamp();

    class OperationImpl implements Operation {

        private final String name;
        private final long timestamp;

        public OperationImpl(String name, long timestamp) {
            this.name = name;
            this.timestamp = timestamp;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public long timestamp() {
            return this.timestamp;
        }
    }

}
