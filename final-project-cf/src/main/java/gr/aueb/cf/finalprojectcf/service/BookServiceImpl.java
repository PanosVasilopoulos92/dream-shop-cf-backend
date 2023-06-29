package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.BookDTO;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.repository.BookRepository;
import gr.aueb.cf.finalprojectcf.service.exceptions.BookBelongToAnotherUserException;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements IBookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * This method adds a book in the database.
     * @param bookDTO   The typical parameter for the bookDTO object.
     * @return  The added book object.
     */
    @Transactional
    @Override
    public Book addBook(BookDTO bookDTO)  {
        Book book = convertToBook(bookDTO);
        return bookRepository.save(book);
    }

    @Transactional
    @Override
    public Book updateBook(BookDTO bookDTO) throws EntityNotFoundException {
        Book book = bookRepository.findBookById(bookDTO.getId());
        if (book == null || book.getUser() != null) throw new EntityNotFoundException(Book.class, bookDTO.getId());

        return bookRepository.save(convertToBook(bookDTO));
    }

    @Transactional
    @Override
    public void deleteBook(Long id) throws EntityNotFoundException, BookBelongToAnotherUserException {
        if (!bookRepository.existsBookByIdAndUserIsNull(id)) throw new BookBelongToAnotherUserException(id);
        if (bookRepository.findBookById(id) == null) throw new EntityNotFoundException(Book.class, id);

        bookRepository.deleteById(id);    // No need for null check.
    }

    @Override
    public Book findBookById(Long id) throws EntityNotFoundException, BookBelongToAnotherUserException {
        Book book = bookRepository.findBookById(id);
        if (book == null) throw new EntityNotFoundException(Book.class, id);

        // I added the bellow if-statement because I don't want an already purchased book to be displayed to another user again.
        // The owner can access it through its list of Books.
        if (!isBookIsAvailable(id)) throw new BookBelongToAnotherUserException(id);

        return book;
    }

    @Override
    public List<Book> findBooksByAuthor(String author) throws EntityNotFoundException {
        List<Book> tmpList = bookRepository.findBookByAuthorIs(author);
        if (tmpList.size() == 0) throw new EntityNotFoundException(Book.class);
        List<Book> books = new ArrayList<>();

        // A check inside the for-loop in order to send to the client only the available books(don't have a user).
        for (Book book : tmpList) {
            if (book.getUser() == null) {
                books.add(book);
            }
        }

        return books;
    }

    @Override
    public List<Book> findBooksByTitle(String title) throws EntityNotFoundException {
        List<Book> tmpList = bookRepository.findBookByTitleStartingWith(title);
        if (tmpList.size() == 0) throw new EntityNotFoundException(Book.class);
        List<Book> books = new ArrayList<>();

        // A check inside the for-loop in order to send to the client only the available books(don't have a user).
        for (Book book : tmpList) {
            if (book.getUser() == null) {
                books.add(book);
            }
        }

        return books;
    }

    @Override
    public List<Book> findBookByPriceIsLessThanEqual(double price) throws EntityNotFoundException {
        List<Book> tmpList = bookRepository.findBookByPriceIsLessThanEqual(price);
        if (tmpList.size() == 0) throw new EntityNotFoundException(Book.class);
        List<Book> books = new ArrayList<>();

        // A check inside the for-loop in order to send to the client only the available books(don't have a user).
        for (Book book : tmpList) {
            if (book.getUser() == null) {
                books.add(book);
            }
        }

        return books;
    }

    @Override
    public List<Book> findBookByPriceIsBetween(double price1, double price2) throws EntityNotFoundException {
        List<Book> tmpList = bookRepository.findBookByPriceIsBetween(price1, price2);
        if (tmpList.size() == 0) throw new EntityNotFoundException(Book.class);
        List<Book> books = new ArrayList<>();

        // A check inside the for-loop in order to send to the client only the available books(don't have a user).
        for (Book book : tmpList) {
            if (book.getUser() == null) {
                books.add(book);
            }
        }

        return books;
    }

    @Override
    public List<Book> findAll() throws EntityNotFoundException {
        // I want to display only the available books.
        List<Book> books = bookRepository.findAllByUserIsNull();
        if (books.size() == 0) throw new EntityNotFoundException(Book.class);

        return books;
    }

    @Override
    public Page<Book> getPaginatedBooks(int page, int size) throws EntityNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAllByUserIsNull(pageable);

    }

    @Override
    public boolean isBookIsAvailable(Long id) throws EntityNotFoundException {
        return bookRepository.existsBookByIdAndUserIsNull(id);
    }

    private static Book convertToBook(BookDTO dto) {
        return new Book(dto.getId(), dto.getTitle(), dto.getAuthor(), dto.getPrice(), dto.getPublishedYear(), dto.getDescription());
    }
}
