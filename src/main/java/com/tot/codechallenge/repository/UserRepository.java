package com.tot.codechallenge.repository;

import com.tot.codechallenge.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for handling {@link User} entities.
 * Provides methods to perform CRUD operations and complex queries including pagination
 * for User entities, extending JpaRepository to inherit standard CRUD operations and pagination support.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user by their email address.
   *
   * @param email the email address to search for
   * @return an Optional containing the user if found, or an empty Optional if no user is found with the given email
   */
  Optional<User> findByEmail(String email);

  /**
   * Retrieves a page of users filtered by a search term that matches against their name, surname, or email.
   * This method supports pagination and allows for filtering users based on partial text matches in several fields.
   *
   * @param searchTerm the search term used to filter users, applied in a case-insensitive manner
   * @param pageable the pagination information to dictate the page size and current page number
   * @return a Page of User entities that match the search criteria
   */
  @Query("SELECT u FROM User u WHERE " +
      "(:searchTerm IS NULL OR " +
      "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
  Page<User> findByUserDetails(String searchTerm, Pageable pageable);

}