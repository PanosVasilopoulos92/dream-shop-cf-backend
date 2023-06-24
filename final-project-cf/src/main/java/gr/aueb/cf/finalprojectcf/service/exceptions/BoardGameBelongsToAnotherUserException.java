package gr.aueb.cf.finalprojectcf.service.exceptions;

public class BoardGameBelongsToAnotherUserException extends Exception{

    private static final long serialVersionUID = 1L;

    public BoardGameBelongsToAnotherUserException(Long id) {
        super("Board game with ID: " + id + " belongs to another user and cannot be modified or displayed to other users.");
    }
}
