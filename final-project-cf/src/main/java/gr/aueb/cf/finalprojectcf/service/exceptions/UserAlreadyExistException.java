package gr.aueb.cf.finalprojectcf.service.exceptions;

public class UserAlreadyExistException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistException() {
        super("Sorry, user already exist.");
    }
}
