package gr.aueb.cf.finalprojectcf.rest;

import gr.aueb.cf.finalprojectcf.dto.*;
import gr.aueb.cf.finalprojectcf.model.BoardGame;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.model.User;
import gr.aueb.cf.finalprojectcf.model.VideoGame;
import gr.aueb.cf.finalprojectcf.service.IBoardGameService;
import gr.aueb.cf.finalprojectcf.service.IBookService;
import gr.aueb.cf.finalprojectcf.service.IUserService;
import gr.aueb.cf.finalprojectcf.service.IVideoGameService;
import gr.aueb.cf.finalprojectcf.service.exceptions.*;
import gr.aueb.cf.finalprojectcf.service.util.LoggerUtil;
import gr.aueb.cf.finalprojectcf.validator.UpdateUserValidator;
import gr.aueb.cf.finalprojectcf.validator.UserRegisterValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class UserRestController {
    private final IUserService userService;
    private final IBookService bookService;
    private final IBoardGameService boardGameService;
    private final IVideoGameService videoGameService;
    private final UpdateUserValidator updateUserValidator;
    private final UserRegisterValidator userRegisterValidator;
    private final MessageSource messageSource;
    private MessageSourceAccessor accessor;

    @Autowired
    public UserRestController(IUserService userService, IBookService bookService, IBoardGameService boardGameService, IVideoGameService videoGameService, UpdateUserValidator updateUserValidator,
                              UserRegisterValidator userRegisterValidator, MessageSource messageSource) {
        this.userService = userService;
        this.bookService = bookService;
        this.boardGameService = boardGameService;
        this.videoGameService = videoGameService;
        this.updateUserValidator = updateUserValidator;
        this.userRegisterValidator = userRegisterValidator;
        this.messageSource = messageSource;
    }

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Operation(summary = "Get users by their lastname.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were found.",
                content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DisplayUserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid lastname.",
                content = @Content)
    })
    @GetMapping("/users/find-users")
    public ResponseEntity<List<DisplayUserDTO>> getUsersByLastname(@RequestParam("lastname") String lastname) {
        List<User> users;
        try {
            users = userService.findUsersByLastname(lastname);
            List<DisplayUserDTO> displayUsersDTOS = new ArrayList<>();

            for (User user : users) {
                // Maps every User object to a DTO object in order to be returned to the client a List of DTOs.
                displayUsersDTOS.add(EntityToDTOMapper.mapUserToDisplayUserDTO(user));
            }

            return new ResponseEntity<>(displayUsersDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get user by its username.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisplayUserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User could not be found.",
                    content = @Content)
    })
    @GetMapping("/users/findOne/{username}")
    public ResponseEntity<DisplayUserDTO> getUserByUsername(@PathVariable("username") String username) {
        User user;
        try {
            user = userService.getUserByUsername(username);

            // We transform the user into userDTO in order to send it to the client.
            DisplayUserDTO displayUserDTO = EntityToDTOMapper.mapUserToDisplayUserDTO(user);

            return new ResponseEntity<>(displayUserDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Registers/adds a user to the database.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was successfully registered.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterUserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input was supplied.",
                    content = @Content)
    })
    @PostMapping("/users/register")
    public ResponseEntity<DisplayUserDTO> registerUser(@RequestBody RegisterUserDTO dto,
                                                        BindingResult bindingResult) {
        userRegisterValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInRegistration"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User user = userService.registerUser(dto);
            DisplayUserDTO displayUserDTO = EntityToDTOMapper.mapUserToDisplayUserDTO(user);     // Map in order to return to the client a DTO which does not contain the encrypted password info.
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(displayUserDTO.getId())
                    .toUri();

            return ResponseEntity.created(location).body(displayUserDTO);
        } catch (UserAlreadyExistException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Updates a user.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was successfully updated.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateUserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data was provided.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found.",
                    content = @Content)
    })
    @PutMapping("/users/update/{userId}")
    public ResponseEntity<DisplayUserDTO> updateUser(@PathVariable("userId") Long id,
                                              @RequestBody UpdateUserDTO dto, BindingResult bindingResult) throws EntityNotFoundException {

        updateUserValidator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInUserUpdate"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // What I wanted to accomplish with this, a little complex(unfortunately) if-statement, is the user to be able
        // to update any field he/she wants with only constraints to be that the username he/she inserts does not belong
        // to another user. Before that solution if I wanted to update a field of a user I had also to update the username,
        // because I had to use the "usernameExists()" method.
        if (userService.usernameExists(dto.getUsername()) &&
                (!dto.getUsername().equals(userService.getUserById(id).getUsername()))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Same as above, but now for the email.
        if (userService.emailExists(dto.getEmail()) &&
                (!dto.getEmail().equals(userService.getUserById(id).getEmail()))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            dto.setId(id);
            User user = userService.updateUser(id, dto);
            System.out.println(user.getId());

            DisplayUserDTO displayUserDTO = EntityToDTOMapper.mapUserToDisplayUserDTO(user);      //  maps the User to a DisplayUserDTO.

            return new ResponseEntity<>(displayUserDTO, HttpStatus.OK);
        } catch (UserAlreadyExistException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            LoggerUtil.getCurrentLogger().warning(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletes a user from the database.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was successfully deleted.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisplayUserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User was not found.",
                    content = @Content)
    })
    @DeleteMapping("/users/delete/{username}")
    public ResponseEntity<DisplayUserDTO> deleteUser(@PathVariable("username") String username) {

        try {
            User user = userService.getUserByUsername(username);
            userService.deleteUser(user.getId());

            DisplayUserDTO displayUserDTO = EntityToDTOMapper.mapUserToDisplayUserDTO(user);  // Map in order to return to the client a DTO object.
            return new ResponseEntity<>(displayUserDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Updates the role of a user.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role successfully changed.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found.",
                    content = @Content)
    })
    @PutMapping("/users/{userId}/update-role")
    public ResponseEntity<String> giveRoleToUser(@PathVariable("userId") Long id, @RequestParam String role) {
        try {
            String message = userService.giveRoleToUser(id, role);
            if (message.equals("Not valid input.")) {
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Adds a book to the user's list of books.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was successfully added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Book or user not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong bookId or userId.",
                    content = @Content)
    })
    @PostMapping("/users/{userId}/books/add/{bookId}")
    public ResponseEntity<BookInUserListDTO> addBookToUser(@PathVariable("userId") Long userId,
                                                           @PathVariable("bookId") Long bookId) {

        try {
            Book book = bookService.findBookById(bookId);
            BookInUserListDTO bookInUserListDTO = EntityToDTOMapper.mapBookInUserListToBook(book);

            userService.addBookToUser(userId, bookId);

            return new ResponseEntity<>(bookInUserListDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BookBelongToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("bookNotAvailable"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "removes a book from the user's list of books.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was successfully removed.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Book or user not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong bookId.",
                    content = @Content)
    })
    @DeleteMapping("/users/{userId}/books/delete")
    public ResponseEntity<Void> removeBookFromUser(@PathVariable("userId") Long userId, @RequestParam Long bookId) {
        try {
            userService.removeBookFromUser(userId, bookId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BookBelongToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("bookNotAvailable"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Adds a board game to the user's list of board games.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board game was successfully added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Board game or user not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong board game ID or user ID.",
                    content = @Content)
    })
    @PostMapping("/users/{userId}/boardGames/add")
    public ResponseEntity<BoardGameDTO> addBoardGameToUser(@PathVariable("userId") Long userId, @RequestParam Long boardGameId) {
        try {
            BoardGame boardGame = boardGameService.findBoardGameById(boardGameId);
            BoardGameDTO boardGameDTO = EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame);

            userService.addBoardGameToUser(userId, boardGameId);

            return new ResponseEntity<>(boardGameDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("boardGameNotFound"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ProductHasOwner e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("boardGameNotAvailable"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Deletes a board game from the user's list of board games.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board game was successfully deleted.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Board game or user not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong board game ID or user ID.",
                    content = @Content)
    })
    @DeleteMapping("users/{userId}/boardGames/delete")
    public ResponseEntity<Void> removeBoardGameFromUser(@PathVariable("userId") Long userId, @RequestParam Long boardGameId) {

        try {
            userService.removeBoardGameFromUser(userId, boardGameId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("boardGameNotFound"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BoardGameBelongsToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("boardGameNotAvailable"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Adds a video game to the user's list of video games.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video game was successfully added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Video game or user not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong video game ID or user ID.",
                    content = @Content)
    })
    @PostMapping("/users/{userId}/videoGames/add")
    public ResponseEntity<VideoGameDTO> addVideoGameToUser(@PathVariable("userId") Long userId, @RequestParam Long videoGameId) {
        try {
            VideoGame videoGame = videoGameService.findVideoGameById(videoGameId);
            VideoGameDTO videoGameDTO = EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame);

            userService.addVideoGameToUser(userId, videoGameId);
            return new ResponseEntity<>(videoGameDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("videoGameNotFound"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (VideoGameBelongsToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Deletes a video game from the user's list of video games.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video game was successfully deleted.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Video game or user not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong video game ID or user ID.",
                    content = @Content)
    })
    @DeleteMapping("users/{userId}/videoGames/delete")
    public ResponseEntity<Void> removeVideoGameFromUser(@PathVariable("userId") Long userId, @RequestParam Long videoGameId) {
        try {
            userService.removeVideoGameFromUser(userId, videoGameId);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (VideoGameBelongsToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("videoGameNotFound"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @Operation(summary = "User login")    // Swagger.
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "User login successful.",
//                    content = { @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AuthenticationRequestDTO.class)) }),
//            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
//                    content = @Content)
//    })
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody AuthenticationRequestDTO authRequestDTO, HttpServletRequest request) {
//
//        try {
//            // Creates an authentication token with the provided username and password.
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                    authRequestDTO.getUsername(), authRequestDTO.getPassword()
//            );
//
//            // Sets the details to the authentication token.
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // Performs the authentication.
//            Authentication authentication = authenticationManager.authenticate(authenticationToken);
//
//            // Sets the authentication object to the SecurityContextHolder.
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            String message = "Welcome " + authentication.getName();
//
//            return new ResponseEntity<>(message, HttpStatus.OK);
//        } catch (AuthenticationException e) {
//            return new ResponseEntity<>("Invalid credentials. Access denied.", HttpStatus.UNAUTHORIZED);
//        }
//    }
}
