package ru.practicum.shareit.request.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoFromRequest {
    @NotEmpty
    @Length(max = 1000)
    String description;
}
