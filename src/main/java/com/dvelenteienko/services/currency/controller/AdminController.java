package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.domain.entity.Role;
import com.dvelenteienko.services.currency.domain.entity.User;
import com.dvelenteienko.services.currency.service.RoleService;
import com.dvelenteienko.services.currency.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping(value = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user) throws URISyntaxException {
        userService.saveUser(user);
        return ResponseEntity.created(new URI("/admin/user/create"))
                .body(user);
    }

    @PostMapping(value = "user/{username}/assign-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> assignRolesToUser(@PathVariable String username, @RequestBody Set<String> roleNames) {
        userService.assignRolesToUser(username, roleNames);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> removeUser(@PathVariable String username) {
        userService.removeUser(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/role/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Role> getRole(@PathVariable String name) {
        return ResponseEntity.ok(roleService.getRole(name));
    }

    @PostMapping(value = "/role", produces = MediaType.APPLICATION_JSON_VALUE)
    public Role createRole(@RequestBody Role role) {
        return roleService.saveRole(role);
    }

    @DeleteMapping(value = "/role/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> removeRole(@PathVariable String name) {
        roleService.removeRole(name);
        return ResponseEntity.ok().build();
    }

}
