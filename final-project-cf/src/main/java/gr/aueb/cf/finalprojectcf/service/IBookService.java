package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.BookDTO;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.service.exceptions.BookBelongToAnotherUserException;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBookService {
    Book addBook(BookDTO bookDTO);
    Book updateBook(BookDTO bookDTO) throws EntityNotFoundException;
    void deleteBook(Long id) throws EntityNotFoundException, BookBelongToAnotherUserException;
    Book findBookById(Long id) throws EntityNotFoundException, BookBelongToAnotherUserException;
    List<Book> findBooksByAuthor(String author) throws EntityNotFoundException;
    List<Book> findBooksByTitle(String title) throws EntityNotFoundException;
    List<Book> findBookByPriceIsLessThanEqual(double price) throws EntityNotFoundException;
    List<Book> findBookByPriceIsBetween(double price1, double price2) throws EntityNotFoundException;
    List<Book> findAll() throws EntityNotFoundException;
    Page<Book> getPaginatedBooks(int page, int size) throws EntityNotFoundException;
    boolean isBookIsAvailable(Long id) throws EntityNotFoundException;
}
