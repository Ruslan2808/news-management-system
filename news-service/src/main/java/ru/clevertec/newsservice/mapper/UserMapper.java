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

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

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
