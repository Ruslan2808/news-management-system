package ru.clevertec.authservice.security.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.repository.UserRepository;

/**
 * Class that implements {@link UserDetailsService} and provides a methods for working with the user
 *
 * @author Ruslan Kantsevich
 * */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Finds user by username and builds object of type {@link UserDetails}
     *
     * @param username username by which to find it
     * @return object of type {@link UserDetails} containing core user information
     * @throws UsernameNotFoundException - if the user could not be found or the user has no GrantedAuthority
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username = [%s] not found".formatted(username)));

        return UserDetailsImpl.builder()
                .user(user)
                .build();
    }
}
