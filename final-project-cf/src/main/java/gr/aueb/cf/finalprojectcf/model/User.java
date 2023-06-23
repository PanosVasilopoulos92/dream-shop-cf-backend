package gr.aueb.cf.finalprojectcf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USERS")
public class User implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // Default strategy is "AUTO".
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstname;

    @Column(name = "LASTNAME", nullable = false)
    private String lastname;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "IMG_URL")
    private String imgUrl;      // Holds the location where we can read the file that contains the image.

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)    // We write the 'mappedBy' in the side of the primary table. In this case Teacher is the primary table.
    private List<Book> books = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardGame> boardGames = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoGame> videoGames = new ArrayList<>();


    // Telescoping with the usage of different types of constructor.
    public User(String username, String password, String firstname, String lastname, String email, String phone, String imgUrl) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.imgUrl = imgUrl;
    }

    public User(Long id, String username, String password, String firstname, String lastname, String email, String phone, String imgUrl) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.imgUrl = imgUrl;
    }
}
