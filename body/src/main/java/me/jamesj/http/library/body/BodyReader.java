package me.jamesj.http.library.body;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import me.jamesj.http.library.body.exceptions.BodyParsingException;
import me.jamesj.http.library.body.exceptions.impl.NoBoundaryProvidedException;
import me.jamesj.http.library.body.exceptions.impl.UnknownContentTypeException;
import me.jamesj.http.library.body.impl.FormDataBody;
import me.jamesj.http.library.body.impl.JsonBody;
import me.jamesj.http.library.body.impl.MultiPartFormDataBody;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("UnstableApiUsage")
public interface BodyReader {

    MediaType MULTIPART_FORM_DATA = MediaType.create("multipart", "form-data");
    MediaType MULTIPART_MIXED = MediaType.create("multipart", "mixed");

    Body read(String body, boolean isBase64) throws BodyParsingException;

    static Body read(String body, boolean isBase64, MediaType mediaType) throws BodyParsingException {
        BodyReader reader;
        if (mediaType.is(MediaType.JSON_UTF_8) || mediaType.is(MediaType.MANIFEST_JSON_UTF_8)) {
            reader = new JsonBody.JsonBodyReader();
        } else if (mediaType.is(MediaType.FORM_DATA)) {
            reader = new FormDataBody.FormDataReader();
        } else if (mediaType.is(BodyReader.MULTIPART_FORM_DATA) || mediaType.is(MULTIPART_MIXED)) {
            ImmutableList<String> param = mediaType.parameters().get("boundary");
            if (param == null || param.isEmpty()) {
                throw new NoBoundaryProvidedException();
            }
            String boundary = param.get(0);

            ImmutableList<String> charsetParams = mediaType.parameters().get("charset");
            Charset charset;
            if (!charsetParams.isEmpty()) {
                charset = Charset.forName(charsetParams.get(0));
            } else {
                charset = StandardCharsets.UTF_8;
            }

            reader = new MultiPartFormDataBody.MultiPartFormDataReader(boundary, charset);
        } else {
            throw new UnknownContentTypeException(mediaType);
        }
        return reader.read(body, isBase64);
    }


}
