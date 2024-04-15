package com.tot.codechallenge.controller;

import com.tot.codechallenge.dto.ReservationDTO;
import com.tot.codechallenge.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing reservations.
 * Handles CRUD operations for reservations through HTTP methods.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

  private final ReservationService reservationService;

  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @Operation(summary = "Create a new reservation", description = "Creates a new reservation with the provided reservation data.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Reservation created successfully", content = @Content(schema = @Schema(implementation = ReservationDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid reservation data provided")
  })
  @PostMapping
  public ResponseEntity<ReservationDTO> createReservation(@Validated @RequestBody ReservationDTO reservationDTO) throws Exception {
      ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);
      return ResponseEntity.ok(createdReservation);
  }

  @Operation(summary = "Get all reservations", description = "Retrieves a page of reservations optionally filtered by a date range")
  @GetMapping
  public ResponseEntity<Page<ReservationDTO>> getReservations(@RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @PageableDefault(size = 10) Pageable pageable) {

    Page<ReservationDTO> reservations = reservationService.findReservationsByOptionalDateRange(startDate, endDate, pageable);
    return ResponseEntity.ok(reservations);
  }

  @Operation(summary = "Get a reservation by ID", description = "Retrieves a reservation by its identifier.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Reservation found", content = @Content(schema = @Schema(implementation = ReservationDTO.class))),
      @ApiResponse(responseCode = "404", description = "Reservation not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
    ReservationDTO reservation = reservationService.findReservationById(id);
    return reservation != null ? ResponseEntity.ok(reservation) : ResponseEntity.notFound().build();
  }

  @Operation(summary = "Update a reservation", description = "Updates an existing reservation identified by the ID with the provided new reservation data.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Reservation updated successfully", content = @Content(schema = @Schema(implementation = ReservationDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid reservation data provided"),
      @ApiResponse(responseCode = "404", description = "Reservation not found")
  })
  @PutMapping("/{id}")
  public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO) throws BadRequestException {
    ReservationDTO updatedReservation = reservationService.updateReservation(id, reservationDTO);
    return ResponseEntity.ok(updatedReservation);
  }

  @Operation(summary = "Delete a reservation", description = "Deletes a reservation identified by its ID.")
  @ApiResponse(responseCode = "200", description = "Reservation deleted successfully")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
      reservationService.deleteReservation(id);
      return ResponseEntity.ok().build();
  }

}