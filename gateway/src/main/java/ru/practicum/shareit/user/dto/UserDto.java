package ru.practicum.shareit.user.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "email")
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
