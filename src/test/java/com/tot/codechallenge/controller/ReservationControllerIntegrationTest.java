package com.tot.codechallenge.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tot.codechallenge.model.Reservation;
import com.tot.codechallenge.model.User;
import com.tot.codechallenge.repository.ReservationRepository;
import com.tot.codechallenge.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  @BeforeEach
  void setup() {
    userRepository.save(new User("janedoe@example.com", "Jane", "Doe"));
  }

  @AfterEach
  void tearDown() {
    reservationRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void testCreateReservation() throws Exception {
    String reservationJson = "{\"userEmail\":\"janedoe@example.com\",\"numberOfGuests\":4,\"tablesReserved\":1,\"reservationDate\":\""+ LocalDate.now().plusDays(1) +"\",\"reservationTime\":\"19:00\"}";

    mockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(reservationJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userEmail").value("janedoe@example.com"))
        .andExpect(jsonPath("$.numberOfGuests").value(4));
  }

  @Test
  public void testCreateReservationInvalidTime() throws Exception {
    String reservationJson = "{\"userEmail\":\"janedoe@example.com\",\"numberOfGuests\":2,\"tablesReserved\":1,\"reservationDate\":\"" + LocalDate.now().plusDays(1) + "\",\"reservationTime\":\"17:00\"}";

    mockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(reservationJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateReservationOverbooking() throws Exception {
    String reservationJson = "{\"userEmail\":\"janedoe@example.com\",\"numberOfGuests\":20,\"tablesReserved\":6,\"reservationDate\":\"" + LocalDate.now().plusDays(1) + "\",\"reservationTime\":\"20:00\"}";

    mockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(reservationJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateReservationFullyBooked() throws Exception {
    prepareFullyBooked();

    String reservationJson = "{\"userEmail\":\"janedoe@example.com\",\"numberOfGuests\":4,\"tablesReserved\":1,\"reservationDate\":\"" + LocalDate.now().plusDays(1) + "\",\"reservationTime\":\"19:30\"}";

    mockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(reservationJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateReservation() throws Exception {
    // First, create a reservation to update
    Reservation reservation = prepareReservation();
    String updateJson = "{\"userEmail\":\""+reservation.getUser().getEmail()+"\",\"numberOfGuests\":2,\"tablesReserved\":1,\"reservationDate\":\"" + LocalDate.now().plusDays(1) + "\",\"reservationTime\":\"21:00\"}";

    mockMvc.perform(put("/api/reservations/{id}", reservation.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.numberOfGuests").value(2));
  }

  @Test
  public void testDeleteReservation() throws Exception {
    // First, create a reservation to delete
    Reservation reservation = prepareReservation();

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservations/{id}", reservation.getId()))
        .andExpect(status().isOk());
  }

  @Test
  public void testListReservations() throws Exception {
    prepareFullyBooked();

    mockMvc.perform(get("/api/reservations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.content[0].userEmail").value("test1@example.com"));
  }

  private Reservation prepareReservation() {
    User user1 = new User("test1@example.com", "Bob", "Vance");
    user1 = userRepository.save(user1);

    Reservation reservation1 = new Reservation(user1, 4, 1, LocalDate.now().plusDays(1), LocalTime.of(19,0));
    return reservationRepository.save(reservation1);
  }

  private void prepareFullyBooked() {
    LocalDate reservationDate = LocalDate.now().plusDays(1);
    LocalTime reservationTime = LocalTime.of(19, 0);

    List<User> users = List.of(
        new User("test1@example.com", "Bob", "Vance"),
        new User("test2@example.com", "Stanley", "Hudson"),
        new User("test3@example.com", "Todd", "Packer"),
        new User("test4@example.com", "Kelly", "Kapoor"),
        new User("test5@example.com", "Oscar", "Martinez")
    );

    List<User> savedUsers = userRepository.saveAll(users);

    List<Reservation> reservations = savedUsers.stream()
        .map(user -> new Reservation(user, 2, 1, reservationDate, reservationTime))
        .toList();

    reservationRepository.saveAll(reservations);
  }
}