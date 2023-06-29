package gr.aueb.cf.finalprojectcf.dto;

import gr.aueb.cf.finalprojectcf.model.BoardGame;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.model.User;
import gr.aueb.cf.finalprojectcf.model.VideoGame;

import java.util.ArrayList;
import java.util.List;

public class EntityToDTOMapper {

    public static BookInUserListDTO mapBookInUserListToBook(Book book) {
        BookInUserListDTO bookInUserListDTO = new BookInUserListDTO();
        bookInUserListDTO.setId(book.getId());
        bookInUserListDTO.setTitle(book.getTitle());
        bookInUserListDTO.setAuthor(book.getAuthor());
        bookInUserListDTO.setPublishedYear(book.getPublishedYear());

        return bookInUserListDTO;
    }

    public static DisplayUserDTO mapUserToDisplayUserDTO(User user) {
        DisplayUserDTO displayUserDTO = new DisplayUserDTO();

        displayUserDTO.setId(user.getId());
        displayUserDTO.setUsername(user.getUsername());
        displayUserDTO.setFirstname(user.getFirstname());
        displayUserDTO.setLastname(user.getLastname());
        displayUserDTO.setEmail(user.getEmail());
        displayUserDTO.setRole(user.getRole());
        displayUserDTO.setPhone(user.getPhone());
        displayUserDTO.setImgUrl(user.getImgUrl());

        List<BookInUserListDTO> booksOfUser = new ArrayList<>();
        for (Book book : user.getBooks()) {
            booksOfUser.add(mapBookInUserListToBook(book));
        }
        displayUserDTO.setBooks(booksOfUser);

        List<BoardGameDTO> boardGamesOfUser = new ArrayList<>();
        for (BoardGame boardGame : user.getBoardGames()) {
            boardGamesOfUser.add(mapBoardGameToBoardGameDTO(boardGame));
        }
        displayUserDTO.setBoardGames(boardGamesOfUser);

        List<VideoGameDTO> videoGamesOfUser = new ArrayList<>();
        for (VideoGame videoGame : user.getVideoGames()) {
            videoGamesOfUser.add(mapVideoGameToVideoGameDTO(videoGame));
        }
        displayUserDTO.setVideoGames(videoGamesOfUser);

        return displayUserDTO;
    }

    public static VideoGameDTO mapVideoGameToVideoGameDTO(VideoGame videoGame) {
        VideoGameDTO videoGameDTO = new VideoGameDTO();

        videoGameDTO.setId(videoGame.getId());
        videoGameDTO.setTitle(videoGame.getTitle());
        videoGameDTO.setManufacturer(videoGame.getManufacturer());
        videoGameDTO.setGenre(videoGame.getGenre());
        videoGameDTO.setPrice(videoGame.getPrice());
        videoGameDTO.setPublishedYear(videoGame.getPublishedYear());
        videoGameDTO.setDescription(videoGame.getDescription());

        return videoGameDTO;
    }

    public static BoardGameDTO mapBoardGameToBoardGameDTO(BoardGame boardGame) {
        BoardGameDTO boardGameDTO = new BoardGameDTO();

        boardGameDTO.setId(boardGame.getId());
        boardGameDTO.setTitle(boardGame.getTitle());
        boardGameDTO.setManufacturer(boardGame.getManufacturer());
        boardGameDTO.setPrice(boardGame.getPrice());
        boardGameDTO.setPublishedYear(boardGame.getPublishedYear());
        boardGameDTO.setNumberOfPlayers(boardGame.getNumberOfPlayers());
        boardGameDTO.setDescription(boardGame.getDescription());

        return boardGameDTO;
    }

//    public static BookDTO mapBookToBookDTO(Book book) {
//        BookDTO bookDTO = new BookDTO();
//
//        bookDTO.setId(book.getId());
//        bookDTO.setTitle(book.getTitle());
//        bookDTO.setAuthor(book.getAuthor());
//        bookDTO.setPrice(book.getPrice());
//        bookDTO.setDescription(book.getDescription());
//        bookDTO.setPublishedYear(book.getPublishedYear());
//
//        return bookDTO;
//    }

}
