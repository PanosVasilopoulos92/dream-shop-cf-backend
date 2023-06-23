package gr.aueb.cf.finalprojectcf.repository;

import gr.aueb.cf.finalprojectcf.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById(Long id);    // Derived Query.

    User findByUsernameEquals(String username);    // Equals is commonly used when we have unique fields.

    List<User> findByLastnameStartingWithOrderByLastnameAsc(String lastname);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.username = ?1 AND U.password = ?2")
    boolean isUserValid(String username, String password);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.username = ?1")
    boolean usernameExists(String username);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.email = ?1")
    boolean emailExists(String email);

    @Query("SELECT count(*) < 1 FROM User U")
    boolean isDatabaseEmpty();

}
