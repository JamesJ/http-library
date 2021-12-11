package me.jamesj.http.library.server.headers;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HeaderParser {

    private final List<Header> headers;

    public HeaderParser(String[] unparsed) {
        this(Arrays.asList(unparsed));
    }

    public HeaderParser(List<String> unparsed) {
        this.headers = new ArrayList<>(unparsed.size());

        for (String s : unparsed) {
            this.headers.add(parse(s));
        }
    }

    public Header get(@NotNull String key) {
        for (Header header : headers) {
            if (header.name().equalsIgnoreCase(key)) {
                return header;
            }
        }
        return null;
    }

    public List<Header> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    private Header parse(String line) {
        int colon = line.indexOf(":");
        String name = line.substring(0, colon);
        String value = line.substring(colon + 1).stripLeading();

        Map<String, String> metadata = new HashMap<>();
        if (value.contains(";")) {
            String[] parts = value.split(";");
            value = parts[0];

            if (parts.length > 1) {
                for (int i = 1; i < parts.length; i++) {
                    Map.Entry<String, String> entry = parseMetadata(parts[i]);
                    metadata.put(entry.getKey().stripLeading(), entry.getValue().stripLeading());
                }
            }
        }
        //todo: metadata values
        return new Header.HeaderImpl(name, value, metadata);
    }

    private Map.Entry<String, String> parseMetadata(String data) {
        int index = data.indexOf("=");
        String key = data.substring(0, index);
        String value = data.substring(index + 1);

        if (value.length() >= 2 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
            value = value.substring(1, value.length() - 1);
        }
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
