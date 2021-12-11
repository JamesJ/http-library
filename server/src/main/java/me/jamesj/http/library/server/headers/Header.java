package me.jamesj.http.library.server.headers;

import java.util.Map;

public interface Header {

    String name();

    String value();

    Map<String, String> metadata();

    class HeaderImpl implements Header {
        private final String name, value;
        private final Map<String, String> metadata;

        public HeaderImpl(String name, String value, Map<String, String> metadata) {
            this.name = name;
            this.value = value;
            this.metadata = metadata;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Map<String, String> metadata() {
            return metadata;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return "HeaderImpl{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", metadata=" + metadata +
                    '}';
        }
    }

}
