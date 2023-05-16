package jp.co.axa.apidemo.exceptions;

public class EmailAlreadyExistException extends AppException {

    private static final long serialVersionUID = -672731722490043065L;

    public EmailAlreadyExistException(String message) {
        super(message);
    }
}