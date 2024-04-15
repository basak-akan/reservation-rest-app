package com.tot.codechallenge.service;

import com.tot.codechallenge.dto.UserDTO;
import com.tot.codechallenge.model.User;
import com.tot.codechallenge.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users within the system.
 * This class provides methods to create, update, delete, and retrieve users along with listing all users with pagination.
 */
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  /**
   * Constructs a new instance of UserServiceImpl with a UserRepository.
   *
   * @param userRepository the repository used for user operations
   */
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Lists all users based on a search term and pageable details.
   *
   * @param searchTerm the term to filter users by their name, surname, or email
   * @param pageable pagination information
   * @return a page of UserDTO containing user details
   */
  @Override
  @Transactional(readOnly = true)
  public Page<UserDTO> listAllUsers(String searchTerm, Pageable pageable) {
    Page<User> users = userRepository.findByUserDetails(searchTerm, pageable);
    return users.map(UserDTO::fromEntity);
  }


  /**
   * Checks and retrieves a user by their email address.
   *
   * @param email the email of the user to check
   * @return the found User
   * @throws IllegalArgumentException if no user is found with the provided email
   */
  @Override
  @Transactional(readOnly = true)
  public User checkUserIfExists(String email) throws IllegalArgumentException {
    Optional<User> existingUser = userRepository.findByEmail(email);
    if (existingUser.isPresent()) {
      return existingUser.get();
    } else {
      throw new IllegalArgumentException("User not found with email: " + email);
    }
  }

  /**
   * Updates an existing user identified by userId.
   *
   * @param userId the ID of the user to update
   * @param userDTO the new user data for update
   * @return the updated UserDTO
   */
  @Override
  @Transactional
  public UserDTO updateUser(Long userId, UserDTO userDTO) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    user.setName(userDTO.name());
    user.setSurname(userDTO.surname());
    user.setEmail(userDTO.email());
    user = userRepository.save(user);
    return UserDTO.fromEntity(user);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param userId the ID of the user to delete
   * @throws IllegalArgumentException if no user is found with the provided ID
   */
  @Override
  @Transactional
  public void deleteUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found with ID: " + userId);
    }
    userRepository.deleteById(userId);
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param userId the ID of the user to retrieve
   * @return the UserDTO of the retrieved user
   * @throws IllegalArgumentException if no user is found with the provided ID
   */
  @Override
  @Transactional(readOnly = true)
  public UserDTO getUserById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    return UserDTO.fromEntity(user);
  }

  /**
   * Creates a new user if they do not already exist.
   *
   * @param user the user data to create a new user
   * @return the newly created UserDTO
   * @throws BadRequestException if the user already exists or the data is invalid
   */
  @Override
  @Transactional
  public UserDTO createUser(UserDTO user) throws BadRequestException {
    // Attempt to find an existing user by email
    Optional<User> existingUser = userRepository.findByEmail(user.email());
    if (existingUser.isPresent()) {
      throw new BadRequestException("User already exists");
    } else {
      // User does not exist, proceed to create a new one
      return UserDTO.fromEntity(createNewUser(user));
    }
  }

  private User createNewUser(UserDTO user) throws BadRequestException {
    try {
      return userRepository.save(user.toEntity());
    } catch (DataIntegrityViolationException e) {
      // Handle the case where the email might be duplicate if two requests come in simultaneously
      throw new BadRequestException("A user with the provided email already exists.");
    }
  }

}
