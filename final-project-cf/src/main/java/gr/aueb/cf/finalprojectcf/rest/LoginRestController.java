package gr.aueb.cf.finalprojectcf.rest;

import gr.aueb.cf.finalprojectcf.dto.AuthenticationRequestDTO;
import gr.aueb.cf.finalprojectcf.dto.AuthenticationResponseDTO;
import gr.aueb.cf.finalprojectcf.model.User;
import gr.aueb.cf.finalprojectcf.service.IUserService;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.finalprojectcf.service.util.LoggerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class LoginRestController {

    private final AuthenticationManager authenticationManager;
    private final IUserService userService;

    @Autowired
    public LoginRestController(AuthenticationManager authenticationManager, IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Operation(summary = "User login")    // Swagger.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User login successful.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequestDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody AuthenticationRequestDTO authRequestDTO,
                                                           HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = userService.getUserByUsername(authRequestDTO.getUsername());
            boolean isPasswordValid = isPasswordCorrect(authRequestDTO.getPassword(), user.getPassword());

            if (!user.getUsername().equals(authRequestDTO.getUsername()) ||
                    !isPasswordValid) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            HttpSession session = request.getSession(true);
            String sessionId = session.getId();
            response.addCookie(new Cookie("JSESSIONID", sessionId));

            // Creates an authentication token with the provided username and password.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authRequestDTO.getUsername(), authRequestDTO.getPassword()
            );

            // Sets the details to the authentication token.
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Performs the authentication.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Sets the authentication object to the SecurityContextHolder.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();
            responseDTO.setToken(UUID.randomUUID().toString());
            responseDTO.setExpiresIn(3600);
            responseDTO.setUsername(authRequestDTO.getUsername());

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public boolean isPasswordCorrect(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
