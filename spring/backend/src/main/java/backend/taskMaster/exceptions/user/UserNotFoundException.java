package backend.taskMaster.exceptions.user;

import backend.taskMaster.models.user.User;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {}
}
