package com.fleetmanagement.fleetsystem.repository;

import com.fleetmanagement.fleetsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Spring JPA automatically implement the crud methods
    //No need to write the SQL queries for it

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // As for the above methods we don't need to write the logic of the queries as it is handled by the
    // jpa, using hibernate
}
