package gr.aueb.cf.finalprojectcf.service.exceptions;

public class BookBelongToAnotherUserException extends Exception {
    private static final long serialVersionUID = 1L;

    public BookBelongToAnotherUserException(Long id) {
        super("Book with ID: " + id + " belongs to another user and cannot be modified or displayed to other users.");
    }
}
