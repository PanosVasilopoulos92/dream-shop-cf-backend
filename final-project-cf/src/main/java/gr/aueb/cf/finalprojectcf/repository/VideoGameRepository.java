package gr.aueb.cf.finalprojectcf.repository;

import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.model.VideoGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoGameRepository extends JpaRepository<VideoGame, Long> {

    VideoGame findVideoGameById(Long id);
    List<VideoGame> findVideoGameByTitleStartingWith(String title);
    List<VideoGame> findVideoGameByManufacturer(String manufacturer);

    List<VideoGame> findVideoGameByPriceLessThanEqual(Double price);

    List<VideoGame> findVideoGameByPriceBetween(Double price1, Double price2);

    List<VideoGame> findAllByUserIsNull();

    @Query("SELECT CASE WHEN COUNT(VD) > 0 THEN TRUE ELSE FALSE END FROM VideoGame VD WHERE VD.id = ?1 AND VD.user IS NULL")
    boolean isVideoGameAvailable(Long id);
}
