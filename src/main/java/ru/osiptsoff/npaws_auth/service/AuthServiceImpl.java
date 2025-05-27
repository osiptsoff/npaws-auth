package ru.osiptsoff.npaws_auth.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import ru.osiptsoff.npaws_auth.dto.RefreshDto;
import ru.osiptsoff.npaws_auth.dto.TokenDto;
import ru.osiptsoff.npaws_auth.dto.UserDto;
import ru.osiptsoff.npaws_auth.model.User;
import ru.osiptsoff.npaws_auth.security.jwt.JwtUtility;
import ru.osiptsoff.npaws_auth.service.exception.IncorrectPasswordException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;

    @Override
    public TokenDto authenticate(UserDto dto) throws UsernameNotFoundException,
            IncorrectPasswordException {
        User user = userService.loadUserByUsername(dto.getLogin());

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Given password is incorrect");
        }

        String refreshToken = jwtUtility.generateRefreshToken(user);

        return new TokenDto("refresh", refreshToken);
    }

    @Override
    public RefreshDto refresh(String refreshToken) throws JwtException {
        User user = jwtUtility.parseAndValidateRefreshToken(refreshToken);

        String accessToken = jwtUtility.generateAccessToken(user);

        var result = new RefreshDto();
        result.setToken(new TokenDto("access", accessToken));
        result.setUserId(user.getId());
        result.setRoles(user.getRoles().stream()
            .map(r -> r.getName())
            .toList()
        );

        return result;
    }

    @Override
    public Integer getRefreshTokenExpirationTimeSec() {
        return jwtUtility.getRefreshLifespawn();
    }
}
