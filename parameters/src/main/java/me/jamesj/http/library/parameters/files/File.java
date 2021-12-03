package me.jamesj.http.library.parameters.files;

import com.google.common.net.MediaType;

@SuppressWarnings("UnstableApiUsage")
public interface File {

    String name();

    MediaType mediaType();

    byte[] content();
}
