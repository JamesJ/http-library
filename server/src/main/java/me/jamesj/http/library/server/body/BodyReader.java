package me.jamesj.http.library.server.body;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.body.exceptions.impl.NoBoundaryProvidedException;
import me.jamesj.http.library.server.body.exceptions.impl.UnknownContentTypeException;
import me.jamesj.http.library.server.body.impl.FormDataBody;
import me.jamesj.http.library.server.body.impl.JsonBody;
import me.jamesj.http.library.server.body.impl.MultiPartFormDataBody;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings("UnstableApiUsage")
public interface BodyReader {

    MediaType MULTIPART_FORM_DATA = MediaType.create("multipart", "form-data");
    MediaType MULTIPART_MIXED = MediaType.create("multipart", "mixed");

    Body read(String body, Charset charset) throws BodyParsingException;

    static Body read(String body, MediaType mediaType, Charset charset) throws BodyParsingException {
        BodyReader reader;
        if (mediaType.is(MediaType.JSON_UTF_8) || mediaType.is(MediaType.MANIFEST_JSON_UTF_8)) {
            reader = new JsonBody.JsonBodyReader();
        } else if (mediaType.is(MediaType.FORM_DATA)) {
            reader = new FormDataBody.FormDataReader();
            if (body != null) {
                body = URLDecoder.decode(body, charset);
            }
        } else if (mediaType.is(BodyReader.MULTIPART_FORM_DATA) || mediaType.is(MULTIPART_MIXED)) {
            ImmutableList<String> param = mediaType.parameters().get("boundary");
            if (param == null || param.isEmpty()) {
                throw new NoBoundaryProvidedException();
            }
            String boundary = param.get(0);

            ImmutableList<String> charsetParams = mediaType.parameters().get("charset");
            if (!charsetParams.isEmpty()) {
                charset = Charset.forName(charsetParams.get(0));
            } else {
                charset = StandardCharsets.UTF_8;
            }

            reader = new MultiPartFormDataBody.MultiPartFormDataReader(boundary);
        } else {
            throw new UnknownContentTypeException(mediaType);
        }
        return reader.read(body, charset);
    }


}
