package me.jamesj.http.library.server.body.impl;

import com.google.common.net.HttpHeaders;
import me.jamesj.http.library.server.headers.Header;
import me.jamesj.http.library.server.headers.HeaderParser;
import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.body.BodyReader;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.exceptions.impl.NoContentDispositionHeaderProvidedException;
import me.jamesj.http.library.server.body.exceptions.impl.NoEntryNameProvidedException;
import me.jamesj.http.library.server.parameters.Source;
import org.apache.commons.fileupload.MultipartStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class MultiPartFormDataBody implements Body {
    
    private final Map<String, FormContext> map;
    
    public MultiPartFormDataBody(Map<String, FormContext> map) {
        this.map = map;
    }
    
    @Override
    public Source.Result get(String key) {
        System.out.println("Searching for " + key);
        FormContext formContext = this.map.get(key);
        if (formContext == null) {
            return null;
        }
        return new Source.Result(formContext.bytes, formContext.metadata);
    }
    
    @Override
    public int length() {
        return map.size();
    }
    
    public static class MultiPartFormDataReader implements BodyReader {
        
        private final String boundary;
        private final Charset charset;
        
        public MultiPartFormDataReader(String boundary, Charset charset) {
            this.boundary = boundary;
            this.charset = charset;
        }
        
        @Override
        public Body read(String body, boolean isBase64) throws BodyParsingException {
            if (isBase64) {
                body = new String(Base64.getDecoder().decode(body), charset);
            }
            
            Map<String, FormContext> map = new HashMap<>();
            
            MultipartStream multipartStream = new MultipartStream(new ByteArrayInputStream(body.getBytes(charset)), this.boundary.getBytes(charset));
            
            try {
                boolean nextPart = multipartStream.skipPreamble();
                while (nextPart) {
                    String[] unparsed = multipartStream.readHeaders().lines().filter(s -> !s.isBlank()).toArray(String[]::new);
                    HeaderParser headerParser = new HeaderParser(unparsed);
                    
                    Header contentDisposition = headerParser.get(HttpHeaders.CONTENT_DISPOSITION);
                    if (contentDisposition == null) {
                        throw new NoContentDispositionHeaderProvidedException();
                    }
                    Map<String, String> metadata = contentDisposition.metadata();
                    String name = metadata.get("name");
                    if (name == null) {
                        throw new NoEntryNameProvidedException();
                    }
                    
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    multipartStream.readBodyData(outputStream);
                    
                    Header contentType = headerParser.get(HttpHeaders.CONTENT_TYPE);
                    if (contentType != null) {
                        metadata.put("content-type", contentType.value());
                    }
                    FormContext context = new FormContext(outputStream.toByteArray(), headerParser.getHeaders(), metadata);
                    map.put(name, context);
                    
                    nextPart = multipartStream.readBoundary();
                }
            } catch (Exception e) {
                throw new BodyParsingException("Uncaught exception whilst parsing body", e);
            }
            return new MultiPartFormDataBody(map);
        }
    }
    
    static class FormContext {
        
        private final byte[] bytes;
        private final List<Header> headers;
        private final Map<String, String> metadata;
        
        FormContext(byte[] bytes, List<Header> headers, Map<String, String> metadata) {
            this.bytes = bytes;
            this.headers = headers;
            this.metadata = metadata;
        }
        
        public Map<String, String> getMetadata() {
            return metadata;
        }
        
        public byte[] getBytes() {
            return bytes;
        }
        
        public List<Header> getHeaders() {
            return headers;
        }
    }
}
