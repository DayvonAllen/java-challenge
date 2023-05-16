package jp.co.axa.apidemo.exceptions;

public class EmailNotFoundException extends AppException{

    private static final long serialVersionUID = -5782192897363865099L;

    public EmailNotFoundException(String message) {
        super(message);
    }
}
