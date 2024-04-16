package com.tot.codechallenge.repository;

import com.tot.codechallenge.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for handling {@link Reservation} entities.
 * This interface extends JpaRepository, enabling CRUD operations and pagination capabilities
 * for Reservation entities.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

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
  Optional<List<Reservation>> findByUserEmailAndReservationDate(String email, LocalDate reservationDate);
}
