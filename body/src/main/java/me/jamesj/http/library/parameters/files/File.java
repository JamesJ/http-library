package me.jamesj.http.library.parameters.files;

import com.google.common.net.MediaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public interface File {
    
    @Nullable
    String name();
    
    @NotNull
    MediaType mediaType();
    
    @Nullable
    byte[] content();
    
    class FileImpl implements File {
        private final String name;
        private final MediaType mediaType;
        private final byte[] content;
        
        public FileImpl(String name, MediaType mediaType, byte[] content) {
            this.name = name;
            this.mediaType = mediaType;
            this.content = content;
        }
        
        @Override
        public String name() {
            return this.name;
        }
        
        @Override
        public MediaType mediaType() {
            return this.mediaType;
        }
        
        @Override
        public byte[] content() {
            return this.content;
        }
    }
}
