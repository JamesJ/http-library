package me.jamesj.http.library.server.body;

import com.google.common.net.MediaType;
import me.jamesj.http.library.server.body.exceptions.BodyParsingException;
import me.jamesj.http.library.server.parameters.ParameterHolder;
import me.jamesj.http.library.server.parameters.files.File;
import me.jamesj.http.library.server.parameters.parser.Parser;
import me.jamesj.http.library.server.parameters.Parameter;
import me.jamesj.http.library.server.parameters.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Testing {

    public static void main(String[] args) {
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=------------------------c41cf8ea7efe14ad");

        String content = "LS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1jNDFjZjhlYTdlZmUxNGFkDQpDb250ZW50LURpc3Bvc2l0aW9uOiBmb3JtLWRhdGE7IG5hbWU9Im5hbWUiDQoNCmphbWVzDQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLWM0MWNmOGVhN2VmZTE0YWQNCkNvbnRlbnQtRGlzcG9zaXRpb246IGZvcm0tZGF0YTsgbmFtZT0iZmlsZSI7IGZpbGVuYW1lPSJmaWxlLnR4dCINCkNvbnRlbnQtVHlwZTogdGV4dC9wbGFpbg0KDQpoaQoNCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tYzQxY2Y4ZWE3ZWZlMTRhZC0tDQo=";

        try {
            Body body = BodyReader.read(content, true, mediaType);
            
            Source.Result result = body.get("file");
            Parameter<File> file = Parameter.name("File attachment")
                .parser(Parser.asFile())
                .source(Source.form("file"))
                .build();
            Parameter<String> name = Parameter.name("Your name")
                .parser(Parser.asString())
                .source(Source.form("name"))
                .build();
            
            Parameter<Integer> age = Parameter.name("Your age")
                .parser(Parser.asInteger())
                .source(Source.form("age"))
                .build();
            
            ParameterHolder parameterHolder = build(body);
            System.out.println("file=" + file.fetch(parameterHolder));
            System.out.println("name=" + name.fetch(parameterHolder));
            System.out.println("age=" + age.fetch(parameterHolder));
        } catch (BodyParsingException e) {
            e.printStackTrace();
        }
    }
    
    private static ParameterHolder build(Body body) {
        return new ParameterHolder() {
            @Override
            public @NotNull Map<String, String[]> headers() {
                return null;
            }
    
            @Override
            public @NotNull Map<String, String[]> query() {
                return null;
            }
    
            @Override
            public @NotNull Map<String, String> pathParams() {
                return null;
            }

            @Override
            public void load() throws BodyParsingException {

            }

            @Override
            public @NotNull Body body() {
                return body;
            }
    
            @Override
            public <T> @Nullable T get(@NotNull Parameter<T> parameter) {
                return null;
            }
        };
    }

}
