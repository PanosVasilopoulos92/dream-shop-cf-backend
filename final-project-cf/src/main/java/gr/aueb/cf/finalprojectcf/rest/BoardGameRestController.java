package gr.aueb.cf.finalprojectcf.rest;

import gr.aueb.cf.finalprojectcf.dto.BoardGameDTO;
import gr.aueb.cf.finalprojectcf.dto.EntityToDTOMapper;
import gr.aueb.cf.finalprojectcf.model.BoardGame;
import gr.aueb.cf.finalprojectcf.service.IBoardGameService;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.exceptions.ProductHasOwner;
import gr.aueb.cf.finalprojectcf.service.util.LoggerUtil;
import gr.aueb.cf.finalprojectcf.validator.BoardGameValidator;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class BoardGameRestController {
    private final IBoardGameService boardGameService;
    private final BoardGameValidator boardGameValidator;
    private final MessageSource messageSource;
    private MessageSourceAccessor accessor;

    @Autowired
    public BoardGameRestController(IBoardGameService boardGameService, BoardGameValidator boardGameValidator, MessageSource messageSource) {
        this.boardGameService = boardGameService;
        this.boardGameValidator = boardGameValidator;
        this.messageSource = messageSource;
    }

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Operation(summary = "Adds a board game to the table 'BOARD_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Board game successfully added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data was provided. Insert failed.",
                    content = @Content)
    })
    @PostMapping("/board-games/create")
    public ResponseEntity<BoardGameDTO> addBoardGame(@RequestBody BoardGameDTO dto, BindingResult bindingResult) {

        boardGameValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInBoardGameInsertion"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BoardGame boardGame = boardGameService.addBoardGame(dto);

        // Map in order to return to the client a DTO object, not the BoardGame object.
        BoardGameDTO boardGameDTO = EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame);

        return new ResponseEntity<>(boardGameDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a board game on the table 'BOARD_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board game successfully updated.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data was provided. Update failed.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board game wasn't found.",
                    content = @Content)
    })
    @PutMapping("/board-games/update/{boardGameId}")
    public ResponseEntity<BoardGameDTO> updateBoardGame(@PathVariable("boardGameId") Long id,
                                                        @RequestBody BoardGameDTO dto, BindingResult bindingResult) {
        boardGameValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("errorInBoardGameUpdate"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            dto.setId(id);
            BoardGame boardGame = boardGameService.updateBoardGame(dto);
            BoardGameDTO boardGameDTO = EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame);

            return new ResponseEntity<>(boardGameDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ProductHasOwner ex) {
            LoggerUtil.getCurrentLogger().warning(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Deletes a board game from the table 'BOARD_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board game successfully deleted.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data was provided. Delete failed.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board game wasn't found.",
                    content = @Content)
    })
    @DeleteMapping("/board-games/delete/{boardGameId}")
    public ResponseEntity<BoardGameDTO> deleteBoardGame(@PathVariable("boardGameId") Long id) {
        try {
            BoardGame boardGame = boardGameService.findBoardGameById(id);
            boardGameService.deleteBoardGame(id);
            BoardGameDTO boardGameDTO = EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame);

            return new ResponseEntity<>(boardGameDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ProductHasOwner e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Gets a board game from the table 'BOARD_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board game was found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Board game belongs to another user.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board game wasn't found.",
                    content = @Content)
    })
    @GetMapping("/board-games/findOne/{boardGameId}")
    public ResponseEntity<BoardGameDTO> findBoardGameById(@PathVariable("boardGameId") Long id) {
        try {
            BoardGame boardGame = boardGameService.findBoardGameById(id);

            // I don't want an already purchased board game to be able to be displayed to another user.
            if (boardGame.getUser() == null) {
                BoardGameDTO boardGameDTO = EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame);
                return new ResponseEntity<>(boardGameDTO, HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a board game from the table 'BOARD_GAMES'.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board game was found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Board game belongs to another user.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board game wasn't found.",
                    content = @Content)
    })
    @GetMapping("/board-games/findAll")
    public ResponseEntity<List<BoardGameDTO>> findAllAvailableBoardGames() {
        List<BoardGameDTO> boardGameDTOS = new ArrayList<>();
        try {
            List<BoardGame> boardGames = boardGameService.findAllAvailableBoardGames();

            for (BoardGame boardGame : boardGames) {
                boardGameDTOS.add(EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame));
            }

            return new ResponseEntity<>(boardGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of board games from the table 'BOARD_GAMES' based on the title initials.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no such board game exists in database.",
                    content = @Content)
    })
    @GetMapping("/board-games/find/title")
    public ResponseEntity<List<BoardGameDTO>> findBoardGamesByTitleStartingWith(@RequestParam String title) {
        List<BoardGameDTO> boardGameDTOS = new ArrayList<>();
        try {
            List<BoardGame> boardGames = boardGameService.findBoardGamesByTitleStartingWith(title);
            for (BoardGame boardGame : boardGames) {
               boardGameDTOS.add(EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame));
            }
            return new ResponseEntity<>(boardGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of board games from the table 'BOARD_GAMES' based on the Manufacturer.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Manufacturer not exist or has no products available.",
                    content = @Content)
    })
    @GetMapping("/board-games/find/manufacturer")
    public ResponseEntity<List<BoardGameDTO>> findBoardGamesByManufacturer(@RequestParam String manufacturer) {
        List<BoardGameDTO> boardGameDTOS = new ArrayList<>();
        try {
            List<BoardGame> boardGames = boardGameService.findBoardGamesByManufacturer(manufacturer);
            for (BoardGame boardGame : boardGames) {
                boardGameDTOS.add(EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame));
            }
            return new ResponseEntity<>(boardGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("notValidId"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of board games from the table 'BOARD_GAMES' based on the price.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no board game has price less than that.",
                    content = @Content)
    })
    @GetMapping("/board-games/find/price-tag")
    public ResponseEntity<List<BoardGameDTO>> findBoardGameByPriceIsLessThan(@RequestParam double price) {
        List<BoardGameDTO> boardGameDTOS = new ArrayList<>();
        try {
            List<BoardGame> boardGames = boardGameService.findBoardGameByPriceIsLessThanEqual(price);
            for (BoardGame boardGame : boardGames) {
                boardGameDTOS.add(EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame));
            }
            return new ResponseEntity<>(boardGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("value.findByPrice"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Gets a list of board games from the table 'BOARD_GAMES' based on a price range.")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board games were found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardGameDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Sorry, no board game were found is this price range.",
                    content = @Content)
    })
    @GetMapping("/board-games/find/price-range")
    public ResponseEntity<List<BoardGameDTO>> findBoardGameByPriceIsBetween(@RequestParam double price1,
                                                                            @RequestParam double price2) {
        List<BoardGameDTO> boardGameDTOS = new ArrayList<>();
        try {
            List<BoardGame> boardGames = boardGameService.findBoardGameByPriceIsBetween(price1, price2);
            for (BoardGame boardGame : boardGames) {
                boardGameDTOS.add(EntityToDTOMapper.mapBoardGameToBoardGameDTO(boardGame));
            }
            return new ResponseEntity<>(boardGameDTOS, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("value.findByPriceRange"));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
