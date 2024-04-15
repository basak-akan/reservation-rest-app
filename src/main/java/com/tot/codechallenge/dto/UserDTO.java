package com.tot.codechallenge.dto;

import com.tot.codechallenge.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user details.
 * This record is designed to encapsulate user data transferred between the service layer
 * and other parts of the application or external systems.
 *
 * @param id The unique identifier of the user.
 * @param name The first name of the user.
 * @param surname The surname or last name of the user.
 * @param email The email address of the user.
 */
public record UserDTO (
    Long id,
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    String name,
    @NotBlank(message = "Surname cannot be blank")
    @Size(max = 50, message = "Surname cannot exceed 50 characters")
    String surname,
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    String email) {

  /**
   * Creates a {@link UserDTO} from a {@link User} entity.
   * This static method facilitates the transformation of a persistence entity into a Data Transfer Object
   *
   * @param user The {@link User} entity to transform into a DTO.
   * @return A new {@link UserDTO} instance containing the user's data.
   */
  public static UserDTO fromEntity(User user) {
    return new UserDTO(
        user.getId(),
        user.getName(),
        user.getSurname(),
        user.getEmail()
    );
  }

  /**
   * Converts this {@link UserDTO} into a {@link User} entity.
   * This method allows the DTO to be transformed back into an entity format
   *
   * @return A {@link User} entity populated with data from this DTO.
   */
  public User toEntity() {
    User user = new User();
    user.setId(this.id());
    user.setName(this.name());
    user.setSurname(this.surname());
    user.setEmail(this.email());
    return user;
  }
}
