package gr.aueb.cf.finalprojectcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardGameDTO {

    private Long id;

    private String title;

    private String numberOfPlayers;     // String because i want to be in a form like: "2-4", "1-3" etc.

    private String description;

    private double price;

    private String manufacturer;

    private int publishedYear;
}
