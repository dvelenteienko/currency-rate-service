package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.entity.User;

import java.util.Set;

public interface UserService {

    User getUser(String username);
    User saveUser(User user);
    void assignRolesToUser(String username, Set<String> roleNames);

    void removeUser(String username);
}
