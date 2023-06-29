package gr.aueb.cf.finalprojectcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayUserDTO {
    private Long id;

    private String username;

    private String firstname;

    private  String lastname;

    private String email;

    private String phone;

    private String role; // Holds the location where we can read the file where image exist.

    private String imgUrl;

    private List<BookInUserListDTO> books = new ArrayList<>();

    private List<BoardGameDTO> boardGames = new ArrayList<>();

    private List<VideoGameDTO> videoGames = new ArrayList<>();
}
