package ru.practicum.shareit.user.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserDtoMapper {
    public static UserDto toUserDto(@NonNull User user) {
        return new UserDto.UserDtoBuilder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUserFromDto(@NonNull UserDtoFromRequest userDto) {
        User user = new User();
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        return user;
    }

    public static User toUserFromDto(@NonNull UserDtoFromRequest userDto, @NonNull User user) {
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        return user;
    }
}
