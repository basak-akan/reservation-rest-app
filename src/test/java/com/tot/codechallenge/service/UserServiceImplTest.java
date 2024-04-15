package com.tot.codechallenge.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.PageRequest.of;

import com.tot.codechallenge.dto.UserDTO;
import com.tot.codechallenge.model.User;
import com.tot.codechallenge.repository.UserRepository;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testListAllUsers() {
    Page<User> page = new PageImpl<>(List.of(new User("test@email.com", "Test User")));
    when(userRepository.findByUserDetails(anyString(), any(Pageable.class))).thenReturn(page);

    Page<UserDTO> result = userService.listAllUsers("test", of(0, 10));

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals("Test User", result.getContent().getFirst().name());
  }

  @Test
  void testCreateUser() throws BadRequestException {
    UserDTO newUser = new UserDTO(null, "New User",  "newuser@test.com");
    User savedUser = new User("newuser@test.com", "New User");
    savedUser.setId(1L);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    UserDTO result = userService.createUser(newUser);

    assertNotNull(result);
    assertEquals("New User", result.name());
  }

  @Test
  void testCreateUserExistingEmail() {
    UserDTO newUser = new UserDTO(null, "New User", "existuser@test.com");
    User existingUser = new User("existuser@test.com", "Exist User");
    existingUser.setId(1L);

    when(userRepository.findByEmail("existuser@test.com")).thenReturn(Optional.of(existingUser));

    assertThrows(BadRequestException.class, () -> userService.createUser(newUser));
  }

  @Test
  void testUpdateUserNotFound() {
    UserDTO updateUser = new UserDTO(1L, "Update User", "updateuser@test.com");

    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, updateUser));
  }

  @Test
  void testDeleteUserNotFound() {
    when(userRepository.existsById(1L)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(1L));
  }

  @Test
  void testGetUserByIdNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> userService.getUserById(1L));
  }

  @Test
  void testCreateUserWithDuplicateEmail() {
    UserDTO newUser = new UserDTO(null, "John Doe", "john.doe@example.com");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

    assertThrows(BadRequestException.class, () -> userService.createUser(newUser));
  }

  @Test
  void testUpdateNonExistentUser() {
    UserDTO updateUser = new UserDTO(1L, "John Doe", "john.doe@example.com");
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, updateUser));
  }

  @Test
  void testDeleteNonExistentUser() {
    Long userId = 1L;
    when(userRepository.existsById(userId)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));
  }

  @Test
  void testGetNonExistentUserById() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> userService.getUserById(userId));
  }

}
