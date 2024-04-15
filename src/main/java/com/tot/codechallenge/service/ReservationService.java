package com.tot.codechallenge.service;

import com.tot.codechallenge.dto.ReservationDTO;
import java.time.LocalDate;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface defining the operations for managing reservations in a restaurant reservation system.
 * Provides methods to create, update, delete, and retrieve reservations.
 */
public interface ReservationService {

  /**
   * Creates a new reservation based on the provided reservation data transfer object (DTO).
   *
   * @param reservation the {@link ReservationDTO} containing the reservation details
   * @return the created {@link ReservationDTO} with populated data
   * @throws Exception if the reservation creation process fails due to data validation,
   *                   availability issues, or other business rules
   */
  ReservationDTO createReservation(ReservationDTO reservation) throws Exception;

  /**
   * Updates an existing reservation identified by the reservation ID with the data provided in the reservation DTO.
   *
   * @param reservationId the ID of the reservation to update
   * @param reservationDTO the new reservation data to apply
   * @return the updated {@link ReservationDTO}
   * @throws BadRequestException if the provided data is not valid or the reservation cannot be found
   */
  ReservationDTO updateReservation(Long reservationId, ReservationDTO reservationDTO)
      throws BadRequestException;

  /**
   * Deletes an existing reservation identified by the reservation ID.
   *
   * @param reservationId the ID of the reservation to delete
   */
  void deleteReservation(Long reservationId);

  /**
   * Retrieves a reservation identified by the reservation ID.
   *
   * @param reservationId the ID of the reservation to find
   * @return the retrieved {@link ReservationDTO} if found
   */
  ReservationDTO findReservationById(Long reservationId);

  /**
   * Retrieves all reservations that match the given search term, paginated according to the provided pageable object.
   *
   * @param startDate start date for date range to search
   * @param endDate end date for date range to search
   * @param pageable a {@link Pageable} object specifying the pagination configuration
   * @return a {@link Page} of {@link ReservationDTO} matching the search criteria
   */
  Page<ReservationDTO> findReservationsByOptionalDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

}
