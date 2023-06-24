package gr.aueb.cf.finalprojectcf.dto;

import gr.aueb.cf.finalprojectcf.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {

    private Long id;

    private String title;

    @Size(min = 3, max = 30, message = "Length must be 3-30 characters.")
    private String author;

    private double price;

    private int publishedYear;

    private String description;

}
