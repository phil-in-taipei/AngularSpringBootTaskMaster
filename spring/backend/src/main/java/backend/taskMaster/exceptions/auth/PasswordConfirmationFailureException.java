package backend.taskMaster.exceptions.auth;

public class PasswordConfirmationFailureException extends Exception {
    public PasswordConfirmationFailureException(String message) {
        super(message);
    }

    public PasswordConfirmationFailureException() {}
}
