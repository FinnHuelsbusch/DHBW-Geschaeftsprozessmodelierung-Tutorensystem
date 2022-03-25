package com.dhbw.tutorsystem.role;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    // extra query to find a role by name 
    Optional<Role> findByName(ERole name);
}
