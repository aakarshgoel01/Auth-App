package com.authentication.application.repositories;

import com.authentication.application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    public Optional<User> findByEmail(String email);

    public boolean existsByEmail(String email);


//    List<User> findByEmail(String email);
}
