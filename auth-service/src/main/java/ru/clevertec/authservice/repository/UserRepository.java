package ru.clevertec.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.clevertec.authservice.entity.User;

import java.util.Optional;

/**
 * Interface to perform operations with object of type {@link User}
 *
 * @author Ruslan Kantsevich
 * */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds object of type {@link User} by username
     *
     * @param username username by which to find it
     * @return object of type {@link Optional<User>}
     * */
    Optional<User> findByUsername(String username);
}
