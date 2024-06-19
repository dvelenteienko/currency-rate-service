package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.domain.entity.Role;
import com.dvelenteienko.services.currency.repository.RoleRepository;
import com.dvelenteienko.services.currency.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultRoleService implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRole(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException(String.format("Role '%s' is not found", name)));
    }

    @Override
    public Role saveRole(Role role) {
        Optional<Role> optionalRole = roleRepository.findByName(role.getName());
        if (optionalRole.isPresent()) {
            throw new IllegalArgumentException(String.format("The Role %s is already exists",
                    optionalRole.get().getName()));
        }
        return roleRepository.save(role);
    }

    @Override
    public void removeRole(String name) {
        Optional<Role> optionalRole = roleRepository.findByName(name);
        optionalRole.ifPresent(r -> roleRepository.deleteById(r.getId()));
    }

}
