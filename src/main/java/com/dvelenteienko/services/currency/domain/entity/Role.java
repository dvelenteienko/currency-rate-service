package com.dvelenteienko.services.currency.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "role")
@Builder(setterPrefix = "set")
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    public Role(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;

//    @ManyToMany(mappedBy = "roles")
//    private Set<User> users;
}
