package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.*;
import gr.aueb.cf.finalprojectcf.model.BoardGame;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.model.User;
import gr.aueb.cf.finalprojectcf.model.VideoGame;
import gr.aueb.cf.finalprojectcf.repository.BoardGameRepository;
import gr.aueb.cf.finalprojectcf.repository.BookRepository;
import gr.aueb.cf.finalprojectcf.repository.UserRepository;
import gr.aueb.cf.finalprojectcf.repository.VideoGameRepository;
import gr.aueb.cf.finalprojectcf.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BoardGameRepository boardGameRepository;
    private final VideoGameRepository videoGameRepository;

    @Autowired
    public UserServiceImpl (UserRepository userRepository, BookRepository bookRepository, BoardGameRepository boardGameRepository, VideoGameRepository videoGameRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.boardGameRepository = boardGameRepository;
        this.videoGameRepository = videoGameRepository;
    }

    @Transactional      // Every method that changes the database must be thread-safe.
    @Override
    public User registerUser(RegisterUserDTO registerUserDTO) throws UserAlreadyExistException {
        if (usernameExists(registerUserDTO.getUsername()) || emailExists(registerUserDTO.getEmail())) {
            throw new UserAlreadyExistException();
        }
        User user = convertToUser(registerUserDTO);
        String encryptedPassword = encryptPassword(registerUserDTO.getPassword());
        user.setPassword(encryptedPassword);

        // If the Database is empty(has no users), it automatically makes the first user to register an Admin.
        if (userRepository.isDatabaseEmpty()) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(Long id, UpdateUserDTO updateUserDTO) throws EntityNotFoundException {
        User user = userRepository.findUserById(id);
        if (user == null) throw new EntityNotFoundException(User.class, id);
        String role = user.getRole();   // Without this when updating the user, it makes the field 'role' null.
        user = convertToUser(updateUserDTO);

        // In order to hash the updated password.
        String encryptedPassword = encryptPassword(updateUserDTO.getPassword());
        user.setPassword(encryptedPassword);
        user.setRole(role);

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);  // No need for null check.
    }

    public String updateRoleOfUser(Long id, String role) throws EntityNotFoundException {
        User user = userRepository.findUserById(id);
        if (user == null) throw new EntityNotFoundException(User.class);
        user.setRole(role);

        if (role.equals("ADMIN")) {
            user.setRole(role);
            userRepository.save(user);
            return user.getUsername() + " is now an admin.";
        } else if (role.equals("USER")) {
            user.setRole(role);
            userRepository.save(user);
            return user.getUsername() + " is now a simple user.";
        } else {
            return "Not valid input.";
        }


    }

    @Override
    public User getUserByUsername(String username) throws EntityNotFoundException {
        User user = userRepository.findByUsernameEquals(username);
        if (user == null) throw new EntityNotFoundException(User.class);

        return user;
    }

    @Override
    public User getUserById(Long id) throws EntityNotFoundException {
        User user = userRepository.findUserById(id);
        if (user == null) throw new EntityNotFoundException(User.class, id);

        return user;
    }

    @Override
    public List<User> findUsersByLastname(String lastname) throws EntityNotFoundException {
        List<User> users = userRepository.findByLastnameStartingWithOrderByLastnameAsc(lastname);
        if (users.size() == 0) throw new EntityNotFoundException(User.class);

        return users;
    }

    @Override
    public List<User> findAllUsers() throws EntityNotFoundException {
        List<User> users = new ArrayList<>();
        users = userRepository.findAll();
        if (users.size() == 0) throw new EntityNotFoundException(User.class);
        return users;
    }

    @Transactional
    @Override
    public void addBookToUser(Long userId, Long bookId) throws EntityNotFoundException, BookBelongToAnotherUserException {

        User user = userRepository.findUserById(userId);
        if (user == null) throw new EntityNotFoundException(User.class, userId);

        Book book = bookRepository.findBookById(bookId);
        if (book == null) throw new EntityNotFoundException(Book.class, bookId);

        if (book.getUser() != null) throw new BookBelongToAnotherUserException(bookId);

        user.getBooks().add(book);
        userRepository.save(user);

        book.setUser(user);
        bookRepository.save(book);
    }

    @Transactional
    @Override
    public void removeBookFromUser(Long userId, Long bookId) throws EntityNotFoundException, BookBelongToAnotherUserException {
        User user = userRepository.findUserById(userId);
        if (user == null) throw new EntityNotFoundException(User.class, userId);

        Book book = bookRepository.findBookById(bookId);

        // If book is null, or does not have a user (it is still available), or the user who tries to delete it is not the owner, an exception will occur.
        if (book == null || book.getUser() == null) throw new EntityNotFoundException(Book.class, bookId);
        if (!book.getUser().getId().equals(userId)) throw new BookBelongToAnotherUserException(bookId);

        user.getBooks().remove(book);
        userRepository.save(user);
//        bookRepository.save(book);
    }

    @Transactional
    @Override
    public void addBoardGameToUser(Long userId, Long boardGameId) throws EntityNotFoundException, ProductHasOwner {
        User user = userRepository.findUserById(userId);
        if (user == null) throw new EntityNotFoundException(User.class, userId);

        BoardGame boardGame = boardGameRepository.findBoardGameById(boardGameId);
        if (boardGame == null) throw new EntityNotFoundException(BoardGame.class, boardGameId);

        // A check in order to verify that the board game to be added is available.
        if (!boardGameRepository.existsBoardGameByIdAndUserIsNull(boardGameId)) throw new ProductHasOwner(boardGameId);

        user.getBoardGames().add(boardGame);
        userRepository.save(user);

        boardGame.setUser(user);
        boardGameRepository.save(boardGame);
    }

    @Transactional
    @Override
    public void removeBoardGameFromUser(Long userId, Long boardGameId) throws EntityNotFoundException, BoardGameBelongsToAnotherUserException {
        User user = userRepository.findUserById(userId);
        if (user == null) throw new EntityNotFoundException(User.class, userId);

        BoardGame boardGame = boardGameRepository.findBoardGameById(boardGameId);

        // If board game is null, or does not have a user (it is still available), or the user who tries to delete it is not the owner, an exception will occur.
        if (boardGame == null || boardGame.getUser() == null) throw new EntityNotFoundException(BoardGame.class, boardGameId);
        if (!boardGame.getUser().getId().equals(userId)) throw new BoardGameBelongsToAnotherUserException(boardGameId);

        user.getBoardGames().remove(boardGame);
        userRepository.save(user);
//        boardGameRepository.save(boardGame);
    }

    @Transactional
    @Override
    public void addVideoGameToUser(Long userId, Long videoGameId) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException {
        User user = userRepository.findUserById(userId);
        if (user == null) throw new EntityNotFoundException(User.class, userId);

        if (!videoGameRepository.isVideoGameAvailable(videoGameId)) {
            throw new VideoGameBelongsToAnotherUserException(videoGameId);
        } else {
            VideoGame videoGame = videoGameRepository.findVideoGameById(videoGameId);
            if (videoGame == null) throw new EntityNotFoundException(VideoGame.class, videoGameId);

            user.getVideoGames().add(videoGame);
            userRepository.save(user);

            videoGame.setUser(user);
            videoGameRepository.save(videoGame);
        }
    }

    @Transactional
    @Override
    public void removeVideoGameFromUser(Long userId, Long videoGameId) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException {
        User user = userRepository.findUserById(userId);
        if (user == null) throw new EntityNotFoundException(User.class, userId);

        VideoGame videoGame = videoGameRepository.findVideoGameById(videoGameId);

        // If video game is null, or does not have a user (it is still available), or the user who tries to delete it is not the owner an exception will occur.
        if (videoGame == null || videoGame.getUser() == null) throw new EntityNotFoundException(VideoGame.class, videoGameId);
        if (!videoGame.getUser().getId().equals(userId)) throw new VideoGameBelongsToAnotherUserException(videoGameId);

        user.getVideoGames().remove(videoGame);
        userRepository.save(user);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.usernameExists(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.emailExists(email);
    }

    @Override
    public String encryptPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    @Override
    public boolean isPasswordCorrect(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    private static User convertToUser(UpdateUserDTO dto) {
        return new User(dto.getId(), dto.getUsername(), dto.getPassword(), dto.getFirstname(),
                dto.getLastname(), dto.getEmail(), dto.getPhone(), dto.getImgUrl());
    }

    private static User convertToUser(RegisterUserDTO dto) {
        return new User(dto.getUsername(), dto.getPassword(), dto.getFirstname(),
                dto.getLastname(), dto.getEmail(), dto.getPhone(), dto.getImgUrl());
    }

}
