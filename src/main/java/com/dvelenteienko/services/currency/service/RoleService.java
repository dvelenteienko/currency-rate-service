package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.entity.Role;
import com.dvelenteienko.services.currency.repository.RoleRepository;

public interface RoleService {

    Role getRole(String name);

    Role saveRole(Role role);

    void removeRole(String name);

}
