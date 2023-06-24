package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.VideoGameDTO;
import gr.aueb.cf.finalprojectcf.model.VideoGame;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.exceptions.VideoGameBelongsToAnotherUserException;

import java.util.List;

public interface IVideoGameService {

    VideoGame addVideoGame(VideoGameDTO videoGameDTO);
    VideoGame updateVideoGame(VideoGameDTO videoGameDTO) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException;
    void deleteVideoGame(Long id) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException;
    VideoGame findVideoGameById(Long id) throws EntityNotFoundException;
    List<VideoGame> findVideoGameByTitleStartingWith(String title) throws EntityNotFoundException;
    List<VideoGame> findVideoGameByManufacturer(String manufacturer) throws EntityNotFoundException;

    List<VideoGame> findVideoGameByPriceLessThanEqual(Double price) throws EntityNotFoundException;

    List<VideoGame> findAll() throws EntityNotFoundException;

    List<VideoGame> findVideoGameByPriceBetween(double price1, double price2) throws EntityNotFoundException;
    boolean isVideoGameAvailable(Long id);

}
