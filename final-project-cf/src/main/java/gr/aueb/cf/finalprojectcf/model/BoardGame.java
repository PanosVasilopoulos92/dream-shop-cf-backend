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
@Table(name = "BOARD_GAMES")
public class BoardGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "NUMBER_OF_PLAYERS", nullable = false)
    private String numberOfPlayers;     // String because i want to be in a form like: "2-4", "1-3" etc.

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "PRICE", nullable = false)
    private double price;

    @Column(name = "MANUFACTURER", nullable = false)
    private String manufacturer;

    @Column(name = "PUBLISHED_YEAR", nullable = false)
    private int publishedYear;

    @ManyToOne
    @JoinColumn(name = "USER_ID")   // The Foreign key inside the 'BOARD_GAMES' table.
    private User user;

    public BoardGame(Long id, String title, String numberOfPlayers, String description, double price, String manufacturer, int publishedYear) {
        this.id = id;
        this.title = title;
        this.numberOfPlayers = numberOfPlayers;
        this.description = description;
        this.price = price;
        this.manufacturer = manufacturer;
        this.publishedYear = publishedYear;
    }
}
