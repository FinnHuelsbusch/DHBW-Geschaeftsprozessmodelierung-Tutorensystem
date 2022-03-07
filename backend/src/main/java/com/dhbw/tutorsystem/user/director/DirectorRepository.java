package com.dhbw.tutorsystem.user.director; 

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<Director, Integer> {

    Optional<Director> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByFirstNameAndLastName(String firstname, String lastname);
}
