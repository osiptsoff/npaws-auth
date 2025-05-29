package ru.osiptsoff.npaws_auth.service;

import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.osiptsoff.npaws_auth.dto.UserDto;
import ru.osiptsoff.npaws_auth.service.exception.IncorrectPasswordException;
import ru.osiptsoff.npaws_auth.service.exception.NoMatchingInfoException;
import ru.osiptsoff.npaws_auth.service.exception.RoleNotExistsException;
import ru.osiptsoff.npaws_auth.service.exception.UsernameAlreadyExistsException;

public interface UserService {
    UserDto changePassword(UserDto dto) throws UsernameNotFoundException, IncorrectPasswordException;
    UserDto save(UserDto dto) throws UsernameAlreadyExistsException, RoleNotExistsException, NoMatchingInfoException;
    UserDto findById(UUID uuid) throws NoMatchingInfoException;
    UserDto setRoles(UserDto dto) throws UsernameNotFoundException, IncorrectPasswordException;
}
