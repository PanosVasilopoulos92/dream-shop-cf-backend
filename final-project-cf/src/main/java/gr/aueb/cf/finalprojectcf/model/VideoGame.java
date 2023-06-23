package gr.aueb.cf.finalprojectcf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "VIDEO_GAMES")
public class VideoGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "GENRE", nullable = false)
    private String genre;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "PRICE", nullable = false)
    private double price;

    @Column(name = "MANUFACTURER", nullable = false)
    private String manufacturer;

    @Column(name = "PUBLISHED_YEAR", nullable = false)
    private int publishedYear;

    @ManyToOne
    @JoinColumn(name = "USER_ID")   // The Foreign key inside the 'VIDEO_GAMES' table.
    private User user;
}
