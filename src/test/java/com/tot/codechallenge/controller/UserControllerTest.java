package com.tot.codechallenge.controller;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.tot.codechallenge.dto.UserDTO;
import com.tot.codechallenge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;

public class UserControllerTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testCreateUser() throws Exception {
    UserDTO userDTO = new UserDTO(1L, "John", "Doe", "john@example.com");
    when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

    ResponseEntity<UserDTO> response = userController.createUser(userDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(userDTO, response.getBody());
  }

  @Test
  void testGetAllUsers() {
    Page<UserDTO> userPage = new PageImpl<>(List.of(new UserDTO(1L, "John", "Doe", "john@example.com")));
    when(userService.listAllUsers(anyString(), any(Pageable.class))).thenReturn(userPage);

    ResponseEntity<Page<UserDTO>> response = userController.getAllUsers("John", Pageable.unpaged());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getTotalElements());
  }

  @Test
  void testGetUserByIdFound() {
    UserDTO userDTO = new UserDTO(1L, "John", "Doe", "john@example.com");
    when(userService.getUserById(1L)).thenReturn(userDTO);

    ResponseEntity<UserDTO> response = userController.getUserById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(userDTO, response.getBody());
  }

  @Test
  void testGetUserByIdNotFound() {
    when(userService.getUserById(anyLong())).thenReturn(null);

    ResponseEntity<UserDTO> response = userController.getUserById(1L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testUpdateUser() throws Exception {
    UserDTO userDTO = new UserDTO(1L, "John", "Updated", "john@example.com");
    when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

    ResponseEntity<UserDTO> response = userController.updateUser(1L, userDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Updated", response.getBody().surname());
  }

  @Test
  void testDeleteUser() {
    doNothing().when(userService).deleteUser(1L);

    ResponseEntity<Void> response = userController.deleteUser(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void testCreateUserThrowsBadRequest() throws BadRequestException {
    UserDTO userDTO = new UserDTO(null, "John", "Doe", "bademail");
    when(userService.createUser(any(UserDTO.class))).thenThrow(new BadRequestException("Invalid data"));

    Exception exception = assertThrows(BadRequestException.class, () -> {
      userController.createUser(userDTO);
    });

    assertEquals("Invalid data", exception.getMessage());
  }

}
