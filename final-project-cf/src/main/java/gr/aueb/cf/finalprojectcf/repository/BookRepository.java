package gr.aueb.cf.finalprojectcf.repository;

import gr.aueb.cf.finalprojectcf.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findBookById(Long id);

    Book findDistinctByTitle(String title);     // Derived Query.

    List<Book> findBookByAuthorIs(String author);

    List<Book> findBookByTitleStartingWith(String title);
    List<Book> findBookByPriceIsLessThanEqual(double price);
    List<Book> findBookByPriceIsBetween(double price1, double price2);

    List<Book> findAllByUserIsNull();

    Page<Book> findAllByUserIsNull(Pageable pageable);

    boolean existsBookByIdAndUserIsNull(Long id);

}
