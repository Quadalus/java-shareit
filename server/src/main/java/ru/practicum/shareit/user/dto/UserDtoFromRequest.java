package ru.practicum.shareit.user.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "email")
public class UserDtoFromRequest {
    private String name;
    private String email;
}
