package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.domain.entity.Role;
import com.dvelenteienko.services.currency.domain.entity.User;
import com.dvelenteienko.services.currency.repository.RoleRepository;
import com.dvelenteienko.services.currency.repository.UserRepository;
import com.dvelenteienko.services.currency.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(String.format("User '%s' is not found", username)));
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void assignRolesToUser(String username, Set<String> roleNames) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            Set<Role> roles = new HashSet<>();
            for (String roleName : roleNames) {
                Optional<Role> optRole = roleRepository.findByName(roleName);
                optRole.ifPresent(roles::add);
            }
            user.setRoles(roles);
            userRepository.save(user);
        }
    }

    @Override
    public void removeUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        optionalUser.ifPresent(u -> userRepository.deleteById(u.getId()));
    }
}
