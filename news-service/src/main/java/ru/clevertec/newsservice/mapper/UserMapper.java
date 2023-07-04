package ru.clevertec.newsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import ru.clevertec.newsservice.dto.response.user.UserResponse;

import java.util.Collection;

/**
 * Class that provides methods for mapping object storing user data
 *
 * @author Ruslan Kantsevich
 * */
@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Mappings object of type {@link UserResponse} storing user data for authentication to
     * object of type {@link UserDetails}
     *
     * @param userResponse object of type {@link UserResponse} containing information about
     *                     username and authorities of user
     * @return object of type {@link UserDetails} containing core user information
     * */
    default UserDetails mapToUserDetails(UserResponse userResponse) {
        Collection<? extends GrantedAuthority> grantedAuthorities =
                Mappers.getMapper(AuthorityMapper.class).mapToGrantedAuthorities(userResponse.getAuthorities());

        return User.builder()
                .username(userResponse.getUsername())
                .password("")
                .authorities(grantedAuthorities)
                .build();
    }
}
