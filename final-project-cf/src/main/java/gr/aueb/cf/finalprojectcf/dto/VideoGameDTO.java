package gr.aueb.cf.finalprojectcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoGameDTO {
    private Long id;

    private String title;

    private String genre;

    private String description;

    private double price;

    private String manufacturer;

    private int publishedYear;

}
