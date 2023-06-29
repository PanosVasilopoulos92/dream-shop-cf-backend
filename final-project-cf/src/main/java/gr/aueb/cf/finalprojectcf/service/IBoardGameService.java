package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.BoardGameDTO;
import gr.aueb.cf.finalprojectcf.model.BoardGame;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.exceptions.ProductHasOwner;

import java.util.List;

public interface IBoardGameService {
    BoardGame addBoardGame (BoardGameDTO boardGameDTO);
    BoardGame updateBoardGame(BoardGameDTO boardGameDTO) throws EntityNotFoundException, ProductHasOwner;
    void deleteBoardGame(Long id) throws EntityNotFoundException, ProductHasOwner;
    BoardGame findBoardGameById(Long id) throws EntityNotFoundException;
    List<BoardGame> findAllAvailableBoardGames() throws EntityNotFoundException;
    List<BoardGame> findBoardGamesByTitleStartingWith(String title) throws EntityNotFoundException;
    List<BoardGame> findBoardGamesByManufacturer(String manufacturer) throws EntityNotFoundException;
    List<BoardGame> findBoardGameByPriceIsLessThanEqual(double price) throws EntityNotFoundException;
    List<BoardGame> findBoardGameByPriceIsBetween(double price, double price2) throws EntityNotFoundException;
    boolean isBoardGameAvailable(Long id);

}
