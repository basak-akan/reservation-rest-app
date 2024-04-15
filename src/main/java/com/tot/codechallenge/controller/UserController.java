package com.tot.codechallenge.controller;

import com.tot.codechallenge.dto.UserDTO;
import com.tot.codechallenge.model.User;
import com.tot.codechallenge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
 * Controller for managing user-related operations.
 * This class provides RESTful web services for creating, retrieving, updating, and deleting users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Create a new user", description = "Creates a new user with the provided user data")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User created successfully",
          content = @Content(schema = @Schema(implementation = UserDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid user data provided")
  })
  @PostMapping
  public ResponseEntity<UserDTO> createUser(@Validated @RequestBody UserDTO userDTO) throws BadRequestException {
      UserDTO createdUser = userService.createUser(userDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @Operation(summary = "Get all users", description = "Retrieves all users optionally filtered by a search term and paginated.")
  @GetMapping
  public ResponseEntity<Page<UserDTO>> getAllUsers(@RequestParam(required = false) String searchTerm,
      @PageableDefault(size = 10) Pageable pageable) {
    Page<UserDTO> users = userService.listAllUsers(searchTerm, pageable);
    return ResponseEntity.ok(users);
  }

  @Operation(summary = "Get a user by their ID")
  @ApiResponse(responseCode = "200", description = "Found the user", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
      UserDTO userDTO = userService.getUserById(id);
      return userDTO != null ? ResponseEntity.ok(userDTO) : ResponseEntity.notFound().build();
  }

  @Operation(summary = "Update a user", description = "Updates an existing user identified by ID with the provided new user data.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid user data provided"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,@Validated @RequestBody UserDTO userDTO) throws BadRequestException {
      UserDTO updatedUser = userService.updateUser(id, userDTO);
      return ResponseEntity.ok(updatedUser);
  }

  @Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
  @ApiResponse(responseCode = "204", description = "User deleted successfully")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
  }
}
