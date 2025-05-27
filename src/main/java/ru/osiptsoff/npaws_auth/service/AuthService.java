package ru.osiptsoff.npaws_auth.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.jsonwebtoken.JwtException;
import ru.osiptsoff.npaws_auth.dto.RefreshDto;
import ru.osiptsoff.npaws_auth.dto.TokenDto;
import ru.osiptsoff.npaws_auth.dto.UserDto;
import ru.osiptsoff.npaws_auth.service.exception.IncorrectPasswordException;

public interface AuthService {
    TokenDto authenticate(UserDto  dto) throws UsernameNotFoundException,
            IncorrectPasswordException;

    RefreshDto refresh(String refreshToken) throws JwtException;

    Integer getRefreshTokenExpirationTimeSec();
}
