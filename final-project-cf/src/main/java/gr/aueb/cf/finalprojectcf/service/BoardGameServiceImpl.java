package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.BoardGameDTO;
import gr.aueb.cf.finalprojectcf.model.BoardGame;
import gr.aueb.cf.finalprojectcf.repository.BoardGameRepository;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.exceptions.ProductHasOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardGameServiceImpl implements IBoardGameService {

    private final BoardGameRepository boardGameRepository;

    @Autowired
    public BoardGameServiceImpl(BoardGameRepository boardGameRepository) {
        this.boardGameRepository = boardGameRepository;
    }

    @Transactional
    @Override
    public BoardGame addBoardGame(BoardGameDTO boardGameDTO) {
        BoardGame boardGame = convertToBoardGame(boardGameDTO);
        return boardGameRepository.save(boardGame);
    }

    @Transactional
    @Override
    public BoardGame updateBoardGame(BoardGameDTO boardGameDTO) throws EntityNotFoundException, ProductHasOwner {
        BoardGame boardGame = boardGameRepository.findBoardGameById(boardGameDTO.getId());
        if (boardGame == null) throw new EntityNotFoundException(BoardGame.class, boardGameDTO.getId());
        if (boardGame.getUser() != null) {
            throw new ProductHasOwner(boardGameDTO.getId());
        } else {
            boardGame = convertToBoardGame(boardGameDTO);
        }

        return boardGameRepository.save(boardGame);
    }

    @Transactional
    @Override
    public void deleteBoardGame(Long id) throws EntityNotFoundException, ProductHasOwner {
        BoardGame boardGame = boardGameRepository.findBoardGameById(id);
        if (boardGame == null) throw new EntityNotFoundException(BoardGame.class, id);
//        if (boardGame.getUser() != null) throw new ProductHasOwner(id);
        if (!isBoardGameAvailable(id)) throw new ProductHasOwner(id);

        boardGameRepository.delete(boardGame);
    }

    @Override
    public BoardGame findBoardGameById(Long id) throws EntityNotFoundException {
        BoardGame boardGame = boardGameRepository.findBoardGameById(id);
        if (boardGame == null) throw new EntityNotFoundException(BoardGame.class, id);

        return boardGame;
    }

    @Override
    public List<BoardGame> findBoardGamesByTitleStartingWith(String title) throws EntityNotFoundException {
        List<BoardGame> tmpList = boardGameRepository.findBoardGamesByTitleStartingWith(title);
        if (tmpList.size() == 0) throw new EntityNotFoundException(BoardGame.class);
        List<BoardGame> boardGames = new ArrayList<>();

        // The for-loop was created in order to send to the client only the available (not having an owner-user) board games.
        for (BoardGame boardGame : tmpList) {
            if (boardGame.getUser() == null) {
                boardGames.add(boardGame);
            }
        }
        return boardGames;
    }

    @Override
    public List<BoardGame> findBoardGamesByManufacturer(String manufacturer) throws EntityNotFoundException {
        List<BoardGame> tmpList = boardGameRepository.findBoardGameByManufacturerIs(manufacturer);
        if (tmpList.size() == 0) throw new EntityNotFoundException(BoardGame.class);
        List<BoardGame> boardGames = new ArrayList<>();

        // The for-loop was created in order to send to the client only the available (not having an owner-user) board games.
        for (BoardGame boardGame : tmpList) {
            if (boardGame.getUser() == null) {
                boardGames.add(boardGame);
            }
        }
        return boardGames;
    }

    @Override
    public List<BoardGame> findBoardGameByPriceIsLessThanEqual(double price) throws EntityNotFoundException {
        List<BoardGame> tmpList = boardGameRepository.findBoardGameByPriceIsLessThanEqual(price);
        if (tmpList.size() == 0) throw new EntityNotFoundException(BoardGame.class);
        List<BoardGame> boardGames = new ArrayList<>();

        // The for-loop was created in order to send to the client only the available (not having an owner-user) board games.
        for (BoardGame boardGame : tmpList) {
            if (boardGame.getUser() == null) {
                boardGames.add(boardGame);
            }
        }
        return boardGames;
    }

    @Override
    public List<BoardGame> findBoardGameByPriceIsBetween(double price1, double price2) throws EntityNotFoundException {
        List<BoardGame> tmpList = boardGameRepository.findBoardGameByPriceIsBetween(price1, price2);
        if (tmpList.size() == 0) throw new EntityNotFoundException(BoardGame.class);
        List<BoardGame> boardGames = new ArrayList<>();

        // The for-loop was created in order to send to the client only the available (not having an owner-user) board games.
        for (BoardGame boardGame : tmpList) {
            if (boardGame.getUser() == null) {
                boardGames.add(boardGame);
            }
        }
        return boardGames;
    }

    @Override
    public boolean isBoardGameAvailable(Long id) {
        return boardGameRepository.existsBoardGameByIdAndUserIsNull(id);
    }

    private static BoardGame convertToBoardGame(BoardGameDTO dto) {
        return new BoardGame(dto.getId(), dto.getTitle(), dto.getNumberOfPlayers(), dto.getDescription(),
                dto.getPrice(), dto.getManufacturer(), dto.getPublishedYear());
    }
}
