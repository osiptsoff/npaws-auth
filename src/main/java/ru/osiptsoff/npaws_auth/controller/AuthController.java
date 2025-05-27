package ru.osiptsoff.npaws_auth.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.osiptsoff.npaws_auth.dto.RefreshDto;
import ru.osiptsoff.npaws_auth.dto.TokenDto;
import ru.osiptsoff.npaws_auth.dto.UserDto;
import ru.osiptsoff.npaws_auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("/authenticate")
    public RefreshDto authenticate(@Valid @RequestBody UserDto dto,
            HttpServletResponse response) {
        TokenDto refreshTokenDto = authService.authenticate(dto);

        var cookie = ResponseCookie.from("refresh", refreshTokenDto.getValue())
            .httpOnly(true)
            .secure(false)
            .maxAge(authService.getRefreshTokenExpirationTimeSec())
            .path("/")
            .domain("localhost")
            .sameSite("lax")
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
        
        return authService.refresh(refreshTokenDto.getValue());
    }

    @GetMapping("/refresh")
    public RefreshDto refresh(@CookieValue(value = "refresh") String token) {
        return authService.refresh(token);
    }
}
