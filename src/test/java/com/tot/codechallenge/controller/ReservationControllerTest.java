package com.tot.codechallenge.controller;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.tot.codechallenge.dto.ReservationDTO;
import com.tot.codechallenge.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;

public class ReservationControllerTest {

  @Mock
  private ReservationService reservationService;

  @InjectMocks
  private ReservationController reservationController;

  private ReservationDTO reservationDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    reservationDTO = new ReservationDTO(1L, "test@example.com", "Test", "User", 4, 1, LocalDate.now().plusDays(1), LocalTime.of(20, 0));
  }

  @Test
  void testCreateReservation() throws Exception {
    when(reservationService.createReservation(any(ReservationDTO.class))).thenReturn(reservationDTO);

    ResponseEntity<ReservationDTO> response = reservationController.createReservation(reservationDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(reservationDTO, response.getBody());
  }

  @Test
  void testGetReservations() {
    Page<ReservationDTO> reservationPage = new PageImpl<>(List.of(reservationDTO));
    when(reservationService.findReservationsByOptionalDateRange(any(), any(), any(Pageable.class))).thenReturn(reservationPage);

    ResponseEntity<Page<ReservationDTO>> response = reservationController.getReservations(LocalDate.now(), LocalDate.now().plusDays(2), Pageable.unpaged());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getTotalElements());
  }

  @Test
  void testGetReservationByIdFound() {
    when(reservationService.findReservationById(1L)).thenReturn(reservationDTO);

    ResponseEntity<ReservationDTO> response = reservationController.getReservationById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(reservationDTO, response.getBody());
  }

  @Test
  void testGetReservationByIdNotFound() {
    when(reservationService.findReservationById(anyLong())).thenReturn(null);

    ResponseEntity<ReservationDTO> response = reservationController.getReservationById(1L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testUpdateReservation() throws Exception {
    when(reservationService.updateReservation(eq(1L), any(ReservationDTO.class))).thenReturn(reservationDTO);

    ResponseEntity<ReservationDTO> response = reservationController.updateReservation(1L, reservationDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(reservationDTO, response.getBody());
  }

  @Test
  void testDeleteReservation() {
    doNothing().when(reservationService).deleteReservation(1L);

    ResponseEntity<Void> response = reservationController.deleteReservation(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testCreateReservationThrowsException() throws Exception {
    when(reservationService.createReservation(any(ReservationDTO.class))).thenThrow(new BadRequestException("Invalid data"));

    Exception exception = assertThrows(BadRequestException.class, () -> {
      reservationController.createReservation(reservationDTO);
    });

    assertEquals("Invalid data", exception.getMessage());
  }

}
