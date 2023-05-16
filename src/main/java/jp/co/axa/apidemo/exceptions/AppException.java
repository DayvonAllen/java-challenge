package jp.co.axa.apidemo.exceptions;

public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}