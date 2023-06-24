package gr.aueb.cf.finalprojectcf.rest;

import gr.aueb.cf.finalprojectcf.dto.BookDTO;
import gr.aueb.cf.finalprojectcf.dto.EntityToDTOMapper;
import gr.aueb.cf.finalprojectcf.dto.VideoGameDTO;
import gr.aueb.cf.finalprojectcf.model.Book;
import gr.aueb.cf.finalprojectcf.model.VideoGame;
import gr.aueb.cf.finalprojectcf.service.IVideoGameService;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.exceptions.VideoGameBelongsToAnotherUserException;
import gr.aueb.cf.finalprojectcf.service.util.LoggerUtil;
import gr.aueb.cf.finalprojectcf.validator.VideoGameValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class VideoGameRestController {
    private final IVideoGameService videoGameService;
    private final VideoGameValidator videoGameValidator;
    private final MessageSource messageSource;
    private MessageSourceAccessor accessor;

    @Autowired
    public VideoGameRestController(IVideoGameService videoGameService, VideoGameValidator videoGameValidator, MessageSource messageSource) {
        this.videoGameService = videoGameService;
        this.videoGameValidator = videoGameValidator;
        this.messageSource = messageSource;
    }

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Operation(summary = "Adds a video game to the table 'VIDEO_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Video game successfully added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data was provided. Insertion failed.",
                    content = @Content)
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/video-games/create")
    public ResponseEntity<VideoGameDTO> addVideoGame(@RequestBody VideoGameDTO dto, BindingResult bindingResult) {

        videoGameValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInVideoGameInsertion"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        VideoGame videoGame = videoGameService.addVideoGame(dto);
        VideoGameDTO videoGameDTO = EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame);

        return new ResponseEntity<>(videoGameDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a video game from the table 'VIDEO_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video game successfully updated.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data was provided. Insertion failed.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Wrong id, video game was not found.",
                    content = @Content)
    })
    @PutMapping("/video-games/update/{videoGameId}")
    public ResponseEntity<VideoGameDTO> updateVideoGame(@PathVariable("videoGameId") Long id,
                                                        @RequestBody VideoGameDTO dto, BindingResult bindingResult) {

        videoGameValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInVideoGameUpdate"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            dto.setId(id);
            VideoGame videoGame = videoGameService.updateVideoGame(dto);
            VideoGameDTO videoGameDTO = EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame);

            return new ResponseEntity<>(videoGameDTO, HttpStatus.OK);
        } catch (VideoGameBelongsToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("videoGameBelongsToAnotherUser"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInVideoGameUpdate"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletes a video game from the table 'VIDEO_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video game successfully deleted.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Wrong id, video game was not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data was provided. Delete failed.",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/video-games/delete/{videoGameId}")
    public ResponseEntity<Void> deleteVideoGame(@PathVariable("videoGameId") Long id) {
        try {
            videoGameService.deleteVideoGame(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (VideoGameBelongsToAnotherUserException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("videoGameHasOwner"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Gets a video game from the table 'VIDEO_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video game was found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Wrong id, video game was not found.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Wrong id was provided.",
                    content = @Content)
    })
    @GetMapping("/video-games/findOne/{videoGameId}")
    public ResponseEntity<VideoGameDTO> findVideoGameById(@PathVariable("videoGameId") Long id) {
        try {
            VideoGame videoGame = videoGameService.findVideoGameById(id);

            // A check in order to display to the client only the video games that are available (don't have user-owner).
            if (videoGame.getUser() != null) {
                LoggerUtil.getCurrentLogger().warning(accessor.getMessage("videoGameHasOwner"));
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                VideoGameDTO videoGameDTO = EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame);
                return new ResponseEntity<>(videoGameDTO, HttpStatus.OK);
            }
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of video games from the table 'VIDEO_GAMES' based on the title initials.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "No video games were found.",
                    content = @Content)
    })
    @GetMapping("/video-games/find/title")
    public ResponseEntity<List<VideoGameDTO>> findVideoGameByTitleStartingWith(@RequestParam String title) {
        List<VideoGameDTO> videoGameDTOS = new ArrayList<>();
        try {
            List<VideoGame> videoGames = videoGameService.findVideoGameByTitleStartingWith(title);

            for( VideoGame videoGame : videoGames) {
                //Maps every Entity to a DTO in order to return to the client a list of DTOs.
                videoGameDTOS.add(EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame));
            }
            return new ResponseEntity<>(videoGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of video games from the table 'VIDEO_GAMES' based on the manufacturer Company.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "No video games were found.",
                    content = @Content)
    })
    @GetMapping("/video-games/find/manufacturer")
    public ResponseEntity<List<VideoGameDTO>> findVideoGameByManufacturer(@RequestParam String manufacturer) {
        List<VideoGameDTO> videoGameDTOS = new ArrayList<>();
        try {
            List<VideoGame> videoGames = videoGameService.findVideoGameByManufacturer(manufacturer);

            for( VideoGame videoGame : videoGames) {
                //Maps every Entity to a DTO in order to return to the client a list of DTOs.
                videoGameDTOS.add(EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame));
            }
            return new ResponseEntity<>(videoGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of video games from the table 'VIDEO_GAMES' based on the price tag.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "No video games were found under the price tag.",
                    content = @Content)
    })
    @GetMapping("/video-games/find/price-tag")
    public ResponseEntity<List<VideoGameDTO>> findVideoGameByPriceLessThanEqual(@RequestParam double price) {
        List<VideoGameDTO> videoGameDTOS = new ArrayList<>();
        try {
            List<VideoGame> videoGames = videoGameService.findVideoGameByPriceLessThanEqual(price);

            for( VideoGame videoGame : videoGames) {
                //Maps every Entity to a DTO in order to return to the client a list of DTOs.
                videoGameDTOS.add(EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame));
            }
            return new ResponseEntity<>(videoGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("value.findByPrice"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of video games from the table 'VIDEO_GAMES' based on a price range.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "No video games were found inside this price range.",
                    content = @Content)
    })
    @GetMapping("/video-games/find/price-range")
    public ResponseEntity<List<VideoGameDTO>> findVideoGameByPriceBetween(@RequestParam double price1,
                                                                          @RequestParam double price2) {
        List<VideoGameDTO> videoGameDTOS = new ArrayList<>();
        try {
            List<VideoGame> videoGames = videoGameService.findVideoGameByPriceBetween(price1, price2);

            for( VideoGame videoGame : videoGames) {
                //Maps every Entity to a DTO in order to return to the client a list of DTOs.
                videoGameDTOS.add(EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame));
            }
            return new ResponseEntity<>(videoGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("value.findByPriceRange"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of all available video games.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VideoGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no video games were found.",
                    content = @Content)
    })
    @GetMapping("/video-games/findAll")
    public ResponseEntity<List<VideoGameDTO>> findAllAvailableVideoGames() {
        List<VideoGameDTO> videoGameDTOS = new ArrayList<>();
        try {
            List<VideoGame> videoGames = videoGameService.findAll();

            for (VideoGame videoGame : videoGames) {
                videoGameDTOS.add(EntityToDTOMapper.mapVideoGameToVideoGameDTO(videoGame));
            }
            return new ResponseEntity<>(videoGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
