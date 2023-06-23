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
@Table(name = "BOOKS")
public class Book implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Column(name = "PRICE", nullable = false)
    private double price;

    @Column(name = "PUBLISHED_YEAR", nullable = false)
    private int publishedYear;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @ManyToOne()
    @JoinColumn(name = "USER_ID")      // Foreign-key.
    private User user;

    public Book(Long id, String title, String author, double price, int publishedYear, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.publishedYear = publishedYear;
        this.description = description;
    }

    public Book(Long id, String title, String author, double price, int publishedYear) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.publishedYear = publishedYear;
    }

    public Book(Long id, double price) {
        this.id = id;
        this.price = price;

    }
}
