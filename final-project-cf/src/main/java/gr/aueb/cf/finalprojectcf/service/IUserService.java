package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.RegisterUserDTO;
import gr.aueb.cf.finalprojectcf.dto.UpdateUserDTO;
import gr.aueb.cf.finalprojectcf.model.User;
import gr.aueb.cf.finalprojectcf.service.exceptions.*;

import java.util.List;

public interface IUserService {

    User registerUser(RegisterUserDTO registerUserDTO) throws UserAlreadyExistException;
    User updateUser(Long id, UpdateUserDTO updateUserDTO) throws EntityNotFoundException, UserAlreadyExistException;
    void deleteUser(Long id);
    User getUserByUsername(String username) throws EntityNotFoundException;
    User getUserById(Long id) throws EntityNotFoundException;
    List<User> findUsersByLastname(String lastname) throws EntityNotFoundException;
    List<User> findAllUsers() throws EntityNotFoundException;
    String encryptPassword(String password);
    boolean isPasswordCorrect(String password, String hashedPassword);
    String giveRoleToUser(Long id, String role) throws EntityNotFoundException;
    void addBookToUser(Long userId, Long bookId) throws EntityNotFoundException, BookBelongToAnotherUserException;
    void removeBookFromUser(Long userId, Long bookId) throws EntityNotFoundException, BookBelongToAnotherUserException;
    void addBoardGameToUser(Long userId, Long boardGameId) throws EntityNotFoundException, ProductHasOwner;
    void removeBoardGameFromUser(Long userId, Long boardGameId) throws EntityNotFoundException, BoardGameBelongsToAnotherUserException;
    void addVideoGameToUser(Long userId, Long videoGameId) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException;
    void removeVideoGameFromUser(Long userId, Long videoGameId) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException;
    boolean usernameExists(String username);
    boolean emailExists(String email);
}
