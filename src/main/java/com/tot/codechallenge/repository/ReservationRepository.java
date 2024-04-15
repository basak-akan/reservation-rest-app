package com.tot.codechallenge.repository;

import com.tot.codechallenge.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for handling {@link Reservation} entities.
 * This interface extends JpaRepository, enabling CRUD operations and pagination capabilities
 * for Reservation entities.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  /**
   * Finds all reservations for a specific date.
   *
   * @param date the reservation date
   * @return a list of reservations that occur on the specified date
   */
  List<Reservation> findByReservationDate(LocalDate date);

  /**
   * Finds a specific reservation by user's email and reservation date.
   * This can be used to check if a user already has a reservation on a specific date.
   *
   * @param email the email of the user
   * @param reservationDate the date of the reservation
   * @return an Optional containing the found reservation if it exists
   */
  Optional<Reservation> findByUserEmailAndReservationDate(String email, LocalDate reservationDate);

  /**
   * Retrieves a page of reservations based on a search term that matches user details.
   * The search term is matched against user's name, surname, and email.
   * This method supports pagination.
   *
   * @param searchTerm the term to match against user details (name, surname, email)
   * @param pageable pagination information
   * @return a page of reservations containing any user details that match the search term
   */
  @Query("SELECT r FROM Reservation r JOIN r.user u WHERE " +
      "(:searchTerm IS NULL OR " +
      "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.surname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
  Page<Reservation> findByUserDetails(String searchTerm, Pageable pageable);

}
