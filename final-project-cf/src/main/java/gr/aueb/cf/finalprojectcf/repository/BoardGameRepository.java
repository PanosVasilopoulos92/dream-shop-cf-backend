package gr.aueb.cf.finalprojectcf.repository;

import gr.aueb.cf.finalprojectcf.model.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {

    BoardGame findBoardGameById(Long id);   // Derived query.

    List<BoardGame> findBoardGamesByTitleStartingWith(String title);

    List<BoardGame> findBoardGameByManufacturerIs(String manufacturer);

    List<BoardGame> findBoardGameByPriceIsLessThanEqual(double price);

    List<BoardGame> findBoardGameByPriceIsBetween(double price, double price2);

    boolean existsBoardGameByIdAndUserIsNull(Long id);

//    @Query("SELECT BG.id, BG.title, BG.price, BG.numberOfPlayers, BG.publishedYear, BG.description FROM BoardGame BG WHERE BG.manufacturer = ?1")
//    List<BoardGame> BoardGamesCreatedByThisManufacturer(String manufacturer);

}
