package com.tot.codechallenge.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tot.codechallenge.model.User;
import com.tot.codechallenge.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  private User savedUser;
  @BeforeEach
  void setup() {
    savedUser = userRepository.save(new User("janedoe@example.com", "Jane Doe"));
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  public void testCreateUser() throws Exception {
    String userJson = "{\"email\":\"alice@example.com\",\"name\":\"Alice\",\"surname\":\"Cooper\"}";

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("alice@example.com"))
        .andExpect(jsonPath("$.name").value("Alice"));
  }

  @Test
  public void testCreateAlreadyExistingUser() throws Exception {
    String userJson = "{\"email\":\"janedoe@example.com\",\"name\":\"Jane\",\"surname\":\"Doe\"}";

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testGetUserById() throws Exception {
    mockMvc.perform(get("/api/users/" + savedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("janedoe@example.com"))
        .andExpect(jsonPath("$.name").value("Jane Doe"));
  }

  @Test
  public void testUpdateUser() throws Exception {
    String updatedUserJson = "{\"email\":\"charlie@example.com\",\"name\":\"Charles\",\"surname\":\"Bucket\"}";

    mockMvc.perform(put("/api/users/" + savedUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatedUserJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Charles"));
  }

  @Test
  public void testDeleteUser() throws Exception {
    mockMvc.perform(delete("/api/users/" + savedUser.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testListUsers() throws Exception {
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].email").value("janedoe@example.com"));
  }

}