package ru.osiptsoff.npaws_auth.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.osiptsoff.npaws_auth.dto.KeyDto;
import ru.osiptsoff.npaws_auth.security.jwt.JwtUtility;

@Service
@RequiredArgsConstructor
public class KeyServiceImpl implements KeyService {
    private final JwtUtility jwtUtility;

    @Override
    public KeyDto getAccessPublicKey() {
        var key = jwtUtility.getAccessPublicKey();

        return new KeyDto(Base64.getEncoder().encodeToString(key.getEncoded()));
    }
    
}
