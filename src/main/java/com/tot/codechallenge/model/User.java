package com.tot.codechallenge.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity representing a user in the system.
 * This class is mapped to the "users" table in the database.
 * It includes details about the user such as their email, name, and surname.
 * It also handles the relationship with reservations, indicating all reservations made by the user.
 */
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String surname;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Reservation> reservations = new HashSet<>();

  /**
   * Default constructor used by JPA.
   */
  public User() {
  }

  public User(String email, String name, String surname) {
    this.email = email;
    this.name = name;
    this.surname = surname;
  }

  // Getters and setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public Set<Reservation> getReservations() {
    return reservations;
  }

  public void setReservations(Set<Reservation> reservations) {
    this.reservations = reservations;
  }

  // Utility method to add reservations
  public void addReservation(Reservation reservation) {
    reservation.setUser(this);
    this.reservations.add(reservation);
  }
}

