package ru.osiptsoff.npaws_auth.dto;

import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Nullable
    private UUID uuid;
    @NotBlank
    private String login;
    @NotBlank
    private String password;
    @Nullable
    private String oldPassword;
    @Nullable
    private Set<String> roles;
}
