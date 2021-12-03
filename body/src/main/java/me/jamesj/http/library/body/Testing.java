package me.jamesj.http.library.body;

import com.google.common.net.MediaType;
import me.jamesj.http.library.body.exceptions.BodyParsingException;

public class Testing {

    public static void main(String[] args) {
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=------------------------8f65d3d218205a81");

        String content = "LS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS04ZjY1ZDNkMjE4MjA1YTgxDQpDb250ZW50LURpc3Bvc2l0aW9uOiBmb3JtLWRhdGE7IG5hbWU9Im9uZSINCg0KdHdvDQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLThmNjVkM2QyMTgyMDVhODENCkNvbnRlbnQtRGlzcG9zaXRpb246IGZvcm0tZGF0YTsgbmFtZT0iZmlsZSI7IGZpbGVuYW1lPSJ0ZXN0LnR4dCINCkNvbnRlbnQtVHlwZTogdGV4dC9wbGFpbg0KDQoiSGVsbG8gZnJvbSBKYW1lcy1QQyIgDQoNCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tOGY2NWQzZDIxODIwNWE4MS0tDQo=";

        try {
            Body body = BodyReader.read(content, true, mediaType);
            System.out.println("read " + body.length() + " body elements");
        } catch (BodyParsingException e) {
            e.printStackTrace();
        }
    }

}
