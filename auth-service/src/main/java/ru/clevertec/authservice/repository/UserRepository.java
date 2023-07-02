package ru.clevertec.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.clevertec.authservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}