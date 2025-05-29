package ru.osiptsoff.npaws_auth.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.osiptsoff.npaws_auth.dto.UserDto;
import ru.osiptsoff.npaws_auth.model.Role;
import ru.osiptsoff.npaws_auth.model.User;
import ru.osiptsoff.npaws_auth.repository.RoleRepository;
import ru.osiptsoff.npaws_auth.repository.UserRepository;
import ru.osiptsoff.npaws_auth.service.exception.IncorrectPasswordException;
import ru.osiptsoff.npaws_auth.service.exception.NoMatchingInfoException;
import ru.osiptsoff.npaws_auth.service.exception.RoleNotExistsException;
import ru.osiptsoff.npaws_auth.service.exception.UsernameAlreadyExistsException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private Map<String, Role> roles;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = userRepository.findByUsername(username);
        if(!userOptional.isPresent()) {
            throw new UsernameNotFoundException("No user with requested username");
        }

        return userOptional.get();
    }

    @Override
    public UserDto changePassword(UserDto dto) throws UsernameNotFoundException, IncorrectPasswordException {
        User user = loadUserByUsername(dto.getLogin());
        if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Given password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        dto.setOldPassword(null);
        dto.setPassword(null);
        return dto;
    }

    @Override
    public UserDto save(UserDto dto) throws UsernameAlreadyExistsException,
            RoleNotExistsException, NoMatchingInfoException {
        if (dto.getUuid() == null) {
            throw new NoMatchingInfoException("New user must be associated with info");
        }
        if (userRepository.existsByUsername(dto.getLogin())) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }
        
        User user = new User();
        user.setRoles(mapRoles(dto.getRoles()));
        user.setUsername(dto.getLogin());
        user.setId(dto.getUuid());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        dto.setPassword(null);
        return dto;
    }

    @Override
    public UserDto findById(UUID uuid) throws NoMatchingInfoException {
        Optional<User> userOptional = userRepository.findById(uuid);
        if (!userOptional.isPresent()) {
            throw new NoMatchingInfoException("User with given id does not exist");
        }

        User user = userOptional.get();
        UserDto dto = new UserDto();
        dto.setLogin(user.getUsername());
        dto.setRoles(user.getRoles().stream()
            .map(r -> r.getName())
            .collect(Collectors.toSet())
        );
        dto.setUuid(user.getId());

        return dto;
    }

    @Override
    public UserDto setRoles(UserDto dto) throws UsernameNotFoundException, IncorrectPasswordException {
        User user = loadUserByUsername(dto.getLogin());

        user.setRoles(mapRoles(dto.getRoles()));
        userRepository.save(user);
        return dto;
    }

    private Set<Role> mapRoles(Collection<String> roleNames) throws RoleNotExistsException {
        if (this.roles == null) {
            loadRoles();
        }

        Set<Role> userRoles = new HashSet<>();
        for (var roleName : roleNames) {
            if (!this.roles.containsKey(roleName)) {
                throw new RoleNotExistsException("Role does not exist");
            }
            userRoles.add(this.roles.get(roleName));
        }
        return userRoles;
    }

    private void loadRoles() {
        Set<Role> bdRoles = roleRepository.findAll();
        this.roles = new HashMap<>();
        bdRoles.forEach(r -> this.roles.put(r.getName(), r));
    }
}
