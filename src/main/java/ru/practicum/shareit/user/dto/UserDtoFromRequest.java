package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoFromRequest {
    private String name;
    @NotNull
    @Email
    private String email;
}
