package com.tot.codechallenge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a reservation in the restaurant reservation system.
 * This class is mapped to the "reservations" table in the database.
 * It includes details about the reservation such as the user who made it,
 * number of guests, number of tables reserved, and the date and time of the reservation.
 */
@Entity
@Table(name = "reservations")
@Getter @Setter @NoArgsConstructor
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "number_of_guests", nullable = false)
  private int numberOfGuests;

  @Column(name = "tables_reserved", nullable = false)
  private int tablesReserved;

  @Column(name = "reservation_date", nullable = false)
  private LocalDate reservationDate;

  @Column(name = "reservation_time", nullable = false)
  private LocalTime reservationTime;


  public Reservation(User user, int numberOfGuests, int tablesReserved, LocalDate reservationDate, LocalTime reservationTime) {
    this.user = user;
    this.numberOfGuests = numberOfGuests;
    this.tablesReserved = tablesReserved;
    this.reservationDate = reservationDate;
    this.reservationTime = reservationTime;
  }

}
