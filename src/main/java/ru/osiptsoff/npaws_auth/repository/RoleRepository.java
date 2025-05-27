package ru.osiptsoff.npaws_auth.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import ru.osiptsoff.npaws_auth.model.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Set<Role> findAll();
}
