package gr.aueb.cf.finalprojectcf.service;

import gr.aueb.cf.finalprojectcf.dto.VideoGameDTO;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.model.VideoGame;
import gr.aueb.cf.finalprojectcf.repository.VideoGameRepository;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.exceptions.VideoGameBelongsToAnotherUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoGameServiceImpl implements IVideoGameService {

    private final VideoGameRepository videoGameRepository;

    @Autowired
    public VideoGameServiceImpl(VideoGameRepository videoGameRepository) {
        this.videoGameRepository = videoGameRepository;
    }

    @Transactional
    @Override
    public VideoGame addVideoGame(VideoGameDTO videoGameDTO) {
        VideoGame videoGame = convertToVideoGame(videoGameDTO);
        return videoGameRepository.save(videoGame);
    }

    @Transactional
    @Override
    public VideoGame updateVideoGame(VideoGameDTO videoGameDTO) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException {
        VideoGame videoGame =  videoGameRepository.findVideoGameById(videoGameDTO.getId());
        if (videoGame == null) throw new EntityNotFoundException(VideoGame.class, videoGameDTO.getId());

        // If a video game has been purchased (has a user), it cannot be modified afterwards.
        if (videoGame.getUser() != null) {
            throw new VideoGameBelongsToAnotherUserException(videoGameDTO.getId());
        } else {
            videoGame = convertToVideoGame(videoGameDTO);
        }
        return videoGameRepository.save(videoGame);
    }

    @Transactional
    @Override
    public void deleteVideoGame(Long id) throws EntityNotFoundException, VideoGameBelongsToAnotherUserException {
        VideoGame videoGame = videoGameRepository.findVideoGameById(id);
        if (videoGame == null) throw new EntityNotFoundException(VideoGame.class, id);
        // A check in order to prevent the deletion of a video game that has an owner. Only the owner-user can delete his/her video games via their list of Video games.
        if (!videoGameRepository.isVideoGameAvailable(id)) throw new VideoGameBelongsToAnotherUserException(id);

        videoGameRepository.delete(videoGame);
    }

    @Override
    public VideoGame findVideoGameById(Long id) throws EntityNotFoundException {
        VideoGame videoGame = videoGameRepository.findVideoGameById(id);
        if (videoGame == null) throw new EntityNotFoundException(VideoGame.class, id);

        return videoGame;
    }

    @Override
    public List<VideoGame> findVideoGameByTitleStartingWith(String title) throws EntityNotFoundException {
        List<VideoGame> tmpList = videoGameRepository.findVideoGameByTitleStartingWith(title);
        if (tmpList.size() == 0) throw new EntityNotFoundException(VideoGame.class);
        List<VideoGame> videoGames = new ArrayList<>();

        // The for-loop was created in order to send to the client only the available (not having an owner-user) video games.
        for (VideoGame videoGame : tmpList) {
            if (videoGame.getUser() == null) {
                videoGames.add(videoGame);
            }
        }

        return videoGames;
    }

    @Override
    public List<VideoGame> findVideoGameByManufacturer(String manufacturer) throws EntityNotFoundException {
        List<VideoGame> tmpList = videoGameRepository.findVideoGameByManufacturer(manufacturer);
        if (tmpList.size() == 0) throw new EntityNotFoundException(VideoGame.class);
        List<VideoGame> videoGames = new ArrayList<>();

        // Same as above.
        for (VideoGame videoGame : tmpList) {
            if (videoGame.getUser() == null) {
                videoGames.add(videoGame);
            }
        }

        return videoGames;
    }

    @Override
    public List<VideoGame> findVideoGameByPriceLessThanEqual(Double price) throws EntityNotFoundException {
        List<VideoGame> tmpList = videoGameRepository.findVideoGameByPriceLessThanEqual(price);
        if (tmpList.size() == 0) throw new EntityNotFoundException(VideoGame.class);
        List<VideoGame> videoGames = new ArrayList<>();

        // The for-loop was created in order to send to the client only the available (not having an owner-user) video games.
        for (VideoGame videoGame : tmpList) {
            if (videoGame.getUser() == null) {
                videoGames.add(videoGame);
            }
        }

        return videoGames;
    }

    @Override
    public List<VideoGame> findAll() throws EntityNotFoundException {
        // I want to display only the available video games.
        List<VideoGame> videoGames = videoGameRepository.findAllByUserIsNull();
        if (videoGames.size() == 0) throw new EntityNotFoundException(Book.class);

        return videoGames;
    }

    @Override
    public List<VideoGame> findVideoGameByPriceBetween(double price1, double price2) throws EntityNotFoundException {
        List<VideoGame> tmpList = videoGameRepository.findVideoGameByPriceBetween(price1, price2);
        if (tmpList.size() == 0) throw new EntityNotFoundException(VideoGame.class);
        List<VideoGame> videoGames = new ArrayList<>();

        // The for-loop was created in order to send to the client only the available (not having an owner-user) video games.
        for (VideoGame videoGame : tmpList) {
            if (videoGame.getUser() == null) {
                videoGames.add(videoGame);
            }
        }

        return videoGames;
    }

    @Override
    public boolean isVideoGameAvailable(Long id) {
        return videoGameRepository.isVideoGameAvailable(id);
    }

    private static VideoGame convertToVideoGame(VideoGameDTO dto) {
        VideoGame videoGame = new VideoGame();

        videoGame.setId(dto.getId());
        videoGame.setTitle(dto.getTitle());
        videoGame.setGenre(dto.getGenre());
        videoGame.setManufacturer(dto.getManufacturer());
        videoGame.setPrice(dto.getPrice());
        videoGame.setPublishedYear(dto.getPublishedYear());
        videoGame.setDescription(dto.getDescription());

        return videoGame;
    }
}
