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

/**
 * Entity representing a reservation in the restaurant reservation system.
 * This class is mapped to the "reservations" table in the database.
 * It includes details about the reservation such as the user who made it,
 * number of guests, number of tables reserved, and the date and time of the reservation.
 */
@Entity
@Table(name = "reservations")
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

  /**
   * Default constructor used by JPA.
   */
  public Reservation() {
  }

  public Reservation(User user, int numberOfGuests, int tablesReserved, LocalDate reservationDate, LocalTime reservationTime) {
    this.user = user;
    this.numberOfGuests = numberOfGuests;
    this.tablesReserved = tablesReserved;
    this.reservationDate = reservationDate;
    this.reservationTime = reservationTime;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDate getReservationDate() {
    return reservationDate;
  }

  public void setReservationDate(LocalDate reservationDate) {
    this.reservationDate = reservationDate;
  }

  public LocalTime getReservationTime() {
    return reservationTime;
  }

  public void setReservationTime(LocalTime reservationTime) {
    this.reservationTime = reservationTime;
  }

  public int getNumberOfGuests() {
    return numberOfGuests;
  }

  public void setNumberOfGuests(int numberOfGuests) {
    this.numberOfGuests = numberOfGuests;
  }

  public int getTablesReserved() {
    return tablesReserved;
  }

  public void setTablesReserved(int tablesReserved) {
    this.tablesReserved = tablesReserved;
  }

}
