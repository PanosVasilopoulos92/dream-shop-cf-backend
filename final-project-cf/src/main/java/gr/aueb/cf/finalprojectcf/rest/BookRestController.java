package gr.aueb.cf.finalprojectcf.rest;

import gr.aueb.cf.finalprojectcf.dto.BookDTO;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.service.IBookService;
import gr.aueb.cf.finalprojectcf.service.exceptions.BookBelongToAnotherUserException;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.util.LoggerUtil;
import gr.aueb.cf.finalprojectcf.validator.BookValidator;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class BookRestController {
    private final IBookService bookService;
    private final BookValidator bookValidator;
    private final MessageSource messageSource;
    private MessageSourceAccessor accessor;

    @Autowired
    public BookRestController(IBookService bookService, BookValidator bookValidator, MessageSource messageSource) {
        this.bookService = bookService;
        this.bookValidator = bookValidator;
        this.messageSource = messageSource;
    }

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Operation(summary = "Get book by its Id.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Book could not be found.",
                    content = @Content)
    })
    @GetMapping("/books/findOne/{bookId}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable("bookId") Long id) {
        Book book;
        try {
            book = bookService.findBookById(id);
            BookDTO bookDTO = mapBookToBookDTO(book);

            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BookBelongToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get book by its title initials.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no books were found.",
                    content = @Content)
    })
    @GetMapping("/books/find/title")
    public ResponseEntity<List<BookDTO>> getBooksByTitle(@RequestParam String title) {     // Was RequestParam()
        List<Book> books;
        try {
            books = bookService.findBooksByTitle(title);
            List<BookDTO> booksDTO = new ArrayList<>();
            for (Book book : books) {
                // A check in order to show only the available books (those which don't belong to a user).
                if (book.getUser() == null) {
                    booksDTO.add(mapBookToBookDTO(book));
                }
            }
            return new ResponseEntity<>(booksDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of book based on the author.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Author name is wrong or no books of this author are available right now.",
                    content = @Content)
    })
    @GetMapping("/books/find/author")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(@RequestParam String author) {
        List<Book> books;
        try {
            books = bookService.findBooksByAuthor(author);
            List<BookDTO> booksDTO = new ArrayList<>();
            for (Book book : books) {
                // A check in order to show only the available books (those which don't belong to a user).
                if (book.getUser() == null) {
                    booksDTO.add(mapBookToBookDTO(book));
                }
            }
            return new ResponseEntity<>(booksDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of book based on the price.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no books cost under that price.",
                    content = @Content)
    })
    @GetMapping("/books/find/price-tag")
    public ResponseEntity<List<BookDTO>> findBookByPriceIsLessThanEqual(@RequestParam double price) {
        List<BookDTO> bookDTOS = new ArrayList<>();
        try {
            List<Book> books = bookService.findBookByPriceIsLessThanEqual(price);

            for (Book book : books) {
                bookDTOS.add(mapBookToBookDTO(book));
            }
            return new ResponseEntity<>(bookDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of book based on the price range.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no books were found in that price range.",
                    content = @Content)
    })
    @GetMapping("/books/find/price-range")
    public ResponseEntity<List<BookDTO>> findBookByPriceIsBetween(@RequestParam double price1, double price2) {
        List<BookDTO> bookDTOS = new ArrayList<>();
        try {
            List<Book> books = bookService.findBookByPriceIsBetween(price1, price2);

            for (Book book : books) {
                bookDTOS.add(mapBookToBookDTO(book));
            }
            return new ResponseEntity<>(bookDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of all available books.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no books were found.",
                    content = @Content)
    })
    @GetMapping("/books/findAll")
    public ResponseEntity<List<BookDTO>> findAllAvailableBooks() {
        List<BookDTO> bookDTOS = new ArrayList<>();
        try {
            List<Book> books = bookService.findAll();

            for (Book book : books) {
                bookDTOS.add(mapBookToBookDTO(book));
            }
            return new ResponseEntity<>(bookDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Adds a book to the table 'BOOKS'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input. Insertion failed.",
                    content = @Content)
    })
    @PostMapping("/books/create")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO dto, BindingResult bindingResult) {

        bookValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInBookInsertion"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Book book = bookService.addBook(dto);
        BookDTO bookDTO = mapBookToBookDTO(book);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bookDTO.getId())
                .toUri();

        return ResponseEntity.created(location).body(bookDTO);
    }

    @Operation(summary = "Updates a book from the table 'BOOKS'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was successfully updated.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input. Update failed.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Wrong id, book was not found.",
                    content = @Content)
    })
    @PutMapping("/books/update/{bookId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable("bookId") Long id,
                                              @RequestBody BookDTO dto, BindingResult bindingResult) {
        bookValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInBookInsertion"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            dto.setId(id);
            if (bookService.isBookIsAvailable(id)) {
                Book book = bookService.updateBook(dto);
                BookDTO bookDTO = mapBookToBookDTO(book);

                return new ResponseEntity<>(bookDTO, HttpStatus.OK);
            } else throw new BookBelongToAnotherUserException(id);

        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BookBelongToAnotherUserException ex) {
            LoggerUtil.getCurrentLogger().warning(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Deletes a book from the table 'BOOKS' if it is not owned by a user.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was successfully deleted.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Wrong id, book was not deleted.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input. Update failed.",
                    content = @Content)
    })
    @DeleteMapping("/books/delete/{bookId}")
    public ResponseEntity<BookDTO> deleteBook(@PathVariable("bookId") Long id) {
        try {
            Book book = bookService.findBookById(id);
            bookService.deleteBook(book.getId());
            BookDTO bookDTO = mapBookToBookDTO(book);     // Map in order to return to the client a DTO.

            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BookBelongToAnotherUserException ex) {
            LoggerUtil.getCurrentLogger().warning(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private static BookDTO mapBookToBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();

        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setPrice(book.getPrice());
        bookDTO.setPublishedYear(book.getPublishedYear());
        bookDTO.setDescription(book.getDescription());

        return bookDTO;
    }
}
