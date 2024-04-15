package com.tot.codechallenge.service;
import static com.tot.codechallenge.service.ReservationServiceImpl.CLOSING_TIME;
import static com.tot.codechallenge.service.ReservationServiceImpl.OPENING_TIME;
import static com.tot.codechallenge.service.ReservationServiceImpl.RESTAURANT_MAX_TABLES;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tot.codechallenge.dto.ReservationDTO;
import com.tot.codechallenge.model.Reservation;
import com.tot.codechallenge.model.User;
import com.tot.codechallenge.repository.ReservationRepository;
import com.tot.codechallenge.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

class ReservationServiceImplTest {

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ReservationServiceImpl reservationService;

  private ReservationDTO reservationDTO;
  private User user;
  private Reservation reservation;

  @BeforeEach
  void setUp() {
    user = new User("test@example.com", "Test", "User");
    reservationDTO = new ReservationDTO(1L, "test@example.com", 4, 1, LocalDate.now().plusDays(1), LocalTime.of(20, 0));
    reservation = new Reservation(user, 4, 1, LocalDate.now().plusDays(1), LocalTime.of(20, 0));
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateReservationSuccessfully() throws Exception {
    when(userService.checkUserIfExists(anyString())).thenReturn(user);
    when(reservationRepository.save(any())).thenReturn(reservation);
    when(userRepository.save(any())).thenReturn(user);

    ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);
    assertNotNull(createdReservation);
    assertEquals(reservationDTO.userEmail(), createdReservation.userEmail());
  }

  @Test
  void testCreateReservationUserNotFound() {
    when(userService.checkUserIfExists(anyString())).thenThrow(new IllegalArgumentException("User not found"));

    assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(reservationDTO));
  }

  @Test
  void testUpdateReservationSuccessfully() throws BadRequestException {
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
    when(reservationRepository.save(any())).thenReturn(reservation);

    ReservationDTO updatedReservation = reservationService.updateReservation(1L, reservationDTO);
    assertNotNull(updatedReservation);
    assertEquals(reservationDTO.tablesReserved(), updatedReservation.tablesReserved());
  }

  @Test
  void testUpdateNonExistentReservation() {
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> reservationService.updateReservation(1L, reservationDTO));
  }

  @Test
  void testDeleteReservationSuccessfully() {
    when(reservationRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(reservationRepository).deleteById(anyLong());

    assertDoesNotThrow(() -> reservationService.deleteReservation(1L));
  }

  @Test
  void testDeleteNonExistentReservation() {
    when(reservationRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> reservationService.deleteReservation(1L));
  }

  @Test
  void testFindReservationByIdSuccessfully() {
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

    ReservationDTO foundReservation = reservationService.findReservationById(1L);
    assertNotNull(foundReservation);
    assertEquals(reservation.getId(), foundReservation.id());
  }

  @Test
  void testFindNonExistentReservationById() {
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> reservationService.findReservationById(1L));
  }

  @Test
  void testReservationAtOpeningTime() {
    reservationDTO = new ReservationDTO(null, "test@example.com", 4, 1, LocalDate.now().plusDays(1), OPENING_TIME);
    when(userService.checkUserIfExists(anyString())).thenReturn(user);
    when(reservationRepository.save(any())).thenReturn(reservation);

    assertDoesNotThrow(() -> reservationService.createReservation(reservationDTO));
  }

  @Test
  void testReservationAtClosingTime() {
    reservationDTO = new ReservationDTO(null, "test@example.com", 4, 1, LocalDate.now().plusDays(1), CLOSING_TIME.minusMinutes(59));
    when(userService.checkUserIfExists(anyString())).thenReturn(user);
    when(reservationRepository.save(any())).thenReturn(reservation);

    BadRequestException thrown = assertThrows(BadRequestException.class, () -> reservationService.createReservation(reservationDTO));
    assertTrue(thrown.getMessage().contains("Invalid reservation time"));
  }

  @Test
  void testReservationOnFullyBookedDay() {
    when(userService.checkUserIfExists(anyString())).thenReturn(user);
    when(reservationRepository.findByReservationDate(any(LocalDate.class))).thenReturn(List.of(new Reservation(user, 4, RESTAURANT_MAX_TABLES, LocalDate.now().plusDays(1), LocalTime.of(19, 0))));

    BadRequestException thrown = assertThrows(BadRequestException.class, () -> reservationService.createReservation(reservationDTO));
    assertTrue(thrown.getMessage().contains("No available tables"));
  }

  @Test
  void testInvalidNumberOfGuests() {
    reservationDTO = new ReservationDTO(null, "test@example.com", 3, 2, LocalDate.now().plusDays(1), LocalTime.of(20, 0)); // 3 guests but 2 tables
    when(userService.checkUserIfExists(anyString())).thenReturn(user);
    when(reservationRepository.save(any())).thenReturn(reservation);

    BadRequestException thrown = assertThrows(BadRequestException.class, () -> reservationService.createReservation(reservationDTO));
    assertTrue(thrown.getMessage().contains("please adapt your reservation accordingly"));
  }

  @Test
  void testReservationInThePast() {
    reservationDTO = new ReservationDTO(null, "test@example.com", 4, 1, LocalDate.now().minusDays(1), LocalTime.of(20, 0));
    when(userService.checkUserIfExists(anyString())).thenReturn(user);

    BadRequestException thrown = assertThrows(BadRequestException.class, () -> reservationService.createReservation(reservationDTO));
    assertTrue(thrown.getMessage().contains("Invalid reservation time"));
  }
}
