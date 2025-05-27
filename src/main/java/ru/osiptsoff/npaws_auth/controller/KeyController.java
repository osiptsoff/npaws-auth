package ru.osiptsoff.npaws_auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.osiptsoff.npaws_auth.dto.KeyDto;
import ru.osiptsoff.npaws_auth.service.KeyService;

@RestController
@RequestMapping("/key")
@RequiredArgsConstructor
/**
 * Сюда ходят другие сервисы
 */
public class KeyController {
    private final KeyService keyService;

    @GetMapping()
    public KeyDto getKey() {
        return keyService.getAccessPublicKey();
    }
}
