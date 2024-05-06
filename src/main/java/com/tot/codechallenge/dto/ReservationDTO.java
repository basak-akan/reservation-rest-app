package com.tot.codechallenge.dto;

import com.tot.codechallenge.model.Reservation;
import com.tot.codechallenge.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Data Transfer Object representing reservation details.
 * This record is used for transferring reservation data between processes and layers,
 * particularly between the service layer and the client.
 *
 * @param id The unique identifier for the reservation.
 * @param userEmail The email of the user who made the reservation.
 * @param numberOfGuests The number of guests included in the reservation.
 * @param tablesReserved The number of tables reserved.
 * @param reservationDate The date of the reservation.
 * @param reservationTime The time of the reservation.
 */
public record ReservationDTO(
    Long id,
    @NotNull(message = "User email must not be null")
    @Email(message = "Invalid email format")
    String userEmail,
    @Min(value = 1, message = "There must be at least one guest")
    int numberOfGuests,
    @Min(value = 1, message = "At least one table must be reserved")
    @Max(value = 5, message = "Cannot reserve more than 5 tables")
    int tablesReserved,
    @NotNull(message = "Reservation date must not be null")
    LocalDate reservationDate,
    @NotNull(message = "Reservation time must not be null")
    LocalTime reservationTime
) {

  /**
   * Converts a {@link Reservation} entity to a {@link ReservationDTO}.
   * This static method is used to abstract and simplify the transformation of entity data into a
   * format suitable for data transfer.
   *
   * @param reservation The reservation entity to convert.
   * @return A new {@link ReservationDTO} object containing the data from the provided reservation entity.
   */
  public static ReservationDTO fromEntity(Reservation reservation) {
    return new ReservationDTO(
        reservation.getId(),
        reservation.getUser().getEmail(),
        reservation.getNumberOfGuests(),
        reservation.getTablesReserved(),
        reservation.getReservationDate(),
        reservation.getReservationTime()
    );
  }

  /**
   * Converts this {@link ReservationDTO} to a {@link Reservation} entity.
   * This method is used to transform the DTO back into an entity, suitable for persistence or further processing.
   * Requires an existing {@link User} entity to associate with the reservation.
   *
   * @param user The user entity associated with the reservation; must be provided externally.
   * @return A new {@link Reservation} entity populated with data from this DTO.
   */
  public Reservation toEntity(User user) {
    Reservation reservation = new Reservation();
    reservation.setUser(user); // Assumes User is provided externally
    reservation.setId(this.id());
    reservation.setNumberOfGuests(this.numberOfGuests());
    reservation.setTablesReserved(this.tablesReserved());
    reservation.setReservationDate(this.reservationDate());
    reservation.setReservationTime(this.reservationTime());
    return reservation;
  }
}
