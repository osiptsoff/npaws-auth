package ru.osiptsoff.npaws_auth.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshDto {
    private TokenDto token;
    private UUID userId;
    private List<String> roles;
}
