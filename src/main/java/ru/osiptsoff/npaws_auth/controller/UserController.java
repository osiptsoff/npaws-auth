package ru.osiptsoff.npaws_auth.controller;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.osiptsoff.npaws_auth.dto.UserDto;
import ru.osiptsoff.npaws_auth.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    @Getter
    private final UserService service;

    @PostMapping("")
    public UserDto create(@Valid @RequestBody UserDto dto) {
        return getService().save(dto);
    }

    @GetMapping("/{id}")
    public UserDto read(@PathVariable UUID id) {
        return getService().findById(id);
    }

    @PutMapping("")
    public UserDto changePassword(@Valid @RequestBody UserDto dto) {
        return getService().changePassword(dto);
    }
}
