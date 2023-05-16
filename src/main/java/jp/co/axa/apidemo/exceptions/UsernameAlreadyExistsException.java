package jp.co.axa.apidemo.exceptions;

public class UsernameAlreadyExistsException extends AppException{

    private static final long serialVersionUID = 5468097376905935769L;

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }

}
