package me.jamesj.http.library.parameters.validation;

public class Failure {

    private final String message;

    public Failure(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static Failure of(String reason) {
        return new Failure(reason);
    }
}
