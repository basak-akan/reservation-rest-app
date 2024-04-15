package com.tot.codechallenge.service;

import com.tot.codechallenge.dto.ReservationDTO;
import com.tot.codechallenge.model.Reservation;
import com.tot.codechallenge.model.User;
import com.tot.codechallenge.repository.ReservationRepository;
import com.tot.codechallenge.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

/**
 * Service implementation for managing reservations.
 * This service handles the creation, retrieval, updating, and deletion of reservations,
 * applying business rules such as availability checks and validation of reservation times.
 */
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;

  private final UserService userService;

  private final UserRepository userRepository;

  protected static final LocalTime OPENING_TIME = LocalTime.of(19, 0);
  protected static final LocalTime CLOSING_TIME = LocalTime.of(23, 59);
  protected static final int RESTAURANT_MAX_TABLES = 5;
  protected static final int MAX_GUESTS_EACH_TABLE = 4;

  public ReservationServiceImpl(ReservationRepository reservationRepository, UserService userService, UserRepository userRepository) {
    this.reservationRepository = reservationRepository;
    this.userService = userService;
    this.userRepository = userRepository;
  }

  /**
   * Creates a reservation based on provided reservation details.
   * Validates user existence, reservation date and time, and table availability.
   *
   * @param reservationDTO the reservation details from client
   * @return the created ReservationDTO with populated data
   * @throws BadRequestException if any validation fails
   */
  @Override
  public ReservationDTO createReservation(ReservationDTO reservationDTO) throws BadRequestException {
    // Validate user existence and get user
    User user = userService.checkUserIfExists(reservationDTO.userEmail());

    // Check if user already has a reservation on the given date
    boolean hasExistingReservation = reservationRepository.findByUserEmailAndReservationDate(
        reservationDTO.userEmail(), reservationDTO.reservationDate()).isPresent();
    if (hasExistingReservation) {
      throw new IllegalStateException("User can only have one reservation per date.");
    }

    validateReservation(reservationDTO);

    Reservation reservation = reservationDTO.toEntity(user);
    user.addReservation(reservation);
    userRepository.save(user);
    reservation = reservationRepository.save(reservation);

    return ReservationDTO.fromEntity(reservation);
  }

  /**
   * Finds reservations optionally filtered by a date range.
   * This method dynamically constructs the query based on the presence of start and/or end dates.
   * It returns a pageable list of reservations that match the criteria.
   *
   * <p>If only the start date is provided, it returns reservations from the start date onwards.
   * If only the end date is provided, it returns reservations up to the end date.
   * If both dates are provided, it returns reservations within the date range.
   * If no dates are provided, it returns all reservations.</p>
   *
   * @param startDate the starting date of the range to filter the reservations (inclusive),
   *                  or null if no start date is to be applied.
   * @param endDate the ending date of the range to filter the reservations (inclusive),
   *                or null if no end date is to be applied.
   * @param pageable the pagination information.
   * @return a {@link Page} of {@link Reservation} objects that match the criteria.
   */
  @Override
  public Page<ReservationDTO> findReservationsByOptionalDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
    Specification<Reservation> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (startDate != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("reservationDate"), startDate));
      }
      if (endDate != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("reservationDate"), endDate));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    return reservationRepository.findAll(spec, pageable).map(ReservationDTO::fromEntity);
  }

  /**
   * Updates an existing reservation with the provided details.
   *
   * @param reservationId the ID of the reservation to update
   * @param reservationDTO the updated reservation details
   * @return the updated reservation data
   * @throws BadRequestException if the reservation cannot be found or if validation fails
   */
  @Override
  public ReservationDTO updateReservation(Long reservationId, ReservationDTO reservationDTO)
      throws BadRequestException {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));

    validateReservation(reservationDTO);
    reservation.setNumberOfGuests(reservationDTO.numberOfGuests());
    reservation.setTablesReserved(reservationDTO.tablesReserved());
    reservation.setReservationDate(reservationDTO.reservationDate());
    reservation.setReservationTime(reservationDTO.reservationTime());
    reservation = reservationRepository.save(reservation);
    return ReservationDTO.fromEntity(reservation);
  }

  /**
   * Deletes a reservation by its ID.
   *
   * @param reservationId the ID of the reservation to delete
   */
  @Override
  public void deleteReservation(Long reservationId) {
    if (!reservationRepository.existsById(reservationId)) {
      throw new IllegalArgumentException("Reservation not found with ID: " + reservationId);
    }
    reservationRepository.deleteById(reservationId);
  }

  /**
   * Retrieves a reservation by its ID.
   *
   * @param reservationId the ID of the reservation to find
   * @return the found reservation data
   */
  @Override
  public ReservationDTO findReservationById(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));
    return ReservationDTO.fromEntity(reservation);
  }

  private boolean isTableAvailable(int requestedTables, LocalDate reservationDate, LocalTime reservationTime) {
    if (requestedTables > RESTAURANT_MAX_TABLES) {
      return false; // More tables requested than available in the restaurant.
    }

    List<Reservation> reservations = reservationRepository.findByReservationDate(reservationDate);
    LocalTime endTime = reservationTime.plusHours(1); // Assume reservation lasts for 1 hour.

    // Calculate the number of tables already reserved in the overlapping time slots.
    int tablesReserved = reservations.stream()
        .filter(reservation -> !reservation.getReservationTime().isAfter(endTime) &&
            !reservation.getReservationTime().plusHours(1).isBefore(reservationTime))
        .mapToInt(Reservation::getTablesReserved)
        .sum();

    // Check if the available tables can accommodate the requested tables.
    return (RESTAURANT_MAX_TABLES - tablesReserved) >= requestedTables;
  }

  private void validateReservation(ReservationDTO reservationDTO) throws BadRequestException {
    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();

    // Validate reservation date and time
    if (!isValidReservationTime(reservationDTO.reservationDate(), reservationDTO.reservationTime(), today, now)) {
      throw new BadRequestException("Invalid reservation time. Please choose a future time within operating hours.");
    }

    // check requested tables and number of guests
    if((reservationDTO.numberOfGuests() + MAX_GUESTS_EACH_TABLE - 1) / MAX_GUESTS_EACH_TABLE != reservationDTO.tablesReserved()) {
      throw new BadRequestException("Each table only have 4 seat, please adapt your reservation accordingly");
    }

    // Check table availability
    if (!isTableAvailable(reservationDTO.tablesReserved(), reservationDTO.reservationDate(), reservationDTO.reservationTime())) {
      throw new BadRequestException("No available tables for the selected time.");
    }

  }

  private boolean isValidReservationTime(LocalDate reservationDate, LocalTime reservationTime, LocalDate today, LocalTime now) {
    if (reservationDate.isBefore(today) || (reservationDate.isEqual(today) && reservationTime.isBefore(now))) {
      return false;  // Reservation is in the past
    }
    return reservationTime.isAfter(OPENING_TIME.minusMinutes(1)) && reservationTime.plusHours(1).isAfter(CLOSING_TIME.plusMinutes(1));
  }

}
