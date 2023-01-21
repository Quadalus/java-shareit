package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class User {
    private Long id;
    private String name;
    @Email
    private String email;
}
