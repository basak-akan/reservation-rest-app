package com.tot.codechallenge.service;

import com.tot.codechallenge.dto.UserDTO;
import com.tot.codechallenge.model.User;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface defining the operations for managing users in a system.
 * Provides methods to create, update, delete, retrieve, and list users with pagination and search functionality.
 */
public interface UserService {

  /**
   * Creates a new user in the system based on the provided UserDTO.
   *
   * @param user the UserDTO containing user information to be created
   * @return the created UserDTO with ID and other generated data
   * @throws BadRequestException if the user data fails validation or if the user already exists
   */
  UserDTO createUser(UserDTO user) throws BadRequestException;

  /**
   * Checks if a user exists based on the provided email address.
   *
   * @param email the email address to check in the system
   * @return the User entity if found
   * @throws IllegalArgumentException if no user is found with the provided email
   */
  User checkUserIfExists(String email) throws IllegalArgumentException;

  /**
   * Updates an existing user identified by userId with the data provided in the userDTO.
   *
   * @param userId the ID of the user to update
   * @param userDTO the UserDTO containing updated user information
   * @return the updated UserDTO
   * @throws BadRequestException if the provided data is not valid or if the user does not exist
   */
  UserDTO updateUser(Long userId, UserDTO userDTO) throws BadRequestException;

  /**
   * Deletes an existing user identified by userId.
   *
   * @param userId the ID of the user to delete
   */
  void deleteUser(Long userId);

  /**
   * Retrieves a user by their userId.
   *
   * @param userId the ID of the user to retrieve
   * @return the UserDTO of the retrieved user
   */
  UserDTO getUserById(Long userId);

  /**
   * Retrieves a paginated list of users that match the given search term.
   *
   * @param searchTerm a string used for searching user details like name, surname, or email
   * @param pageable a Pageable object specifying the pagination configuration
   * @return a Page of UserDTO containing the users that match the search criteria
   */
  Page<UserDTO> listAllUsers(String searchTerm, Pageable pageable);
}
