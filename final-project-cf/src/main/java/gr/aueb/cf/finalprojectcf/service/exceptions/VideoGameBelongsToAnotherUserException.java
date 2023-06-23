package gr.aueb.cf.finalprojectcf.service.exceptions;

public class VideoGameBelongsToAnotherUserException extends Exception{
    private static final long serialVersionUID = 1L;

    public VideoGameBelongsToAnotherUserException(Long id) {
        super("Sorry, video game with ID: " + id + " is not available.");
    }
}
