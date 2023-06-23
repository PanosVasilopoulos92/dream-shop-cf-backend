package gr.aueb.cf.finalprojectcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @Size(min = 5, max = 20, message = "Length must be 5-20 characters.")
    private String username;

    @Size(min = 8, message = "Password must have at least ${min} characters")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d).*$")
    private String password;

    @Size(min = 5, max = 20, message = "Length must be 5-20 characters.")
    private String firstname;

    @Size(min = 5, max = 20, message = "Length must be 5-20 characters.")
    private  String lastname;

    @Email
    private String email;

    @Size(min = 10, max = 10, message = "Phone number must have 10 digits.")
    private String phone;

    private String role;

    private String imgUrl;      // Holds the location where we can read the file where image exist.

    private List<BookInUserListDTO> books = new ArrayList<>();

    private List<BoardGameDTO> boardGames = new ArrayList<>();

    private List<VideoGameDTO> videoGames = new ArrayList<>();
}
