package ru.clevertec.newsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Class that provides methods for mapping user authorities
 *
 * @author Ruslan Kantsevich
 * */
@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorityMapper {

    /**
     * Mappings user authorities of type {@link String} to authorities of type {@link GrantedAuthority}
     *
     * @param authorities list of user authorities of type {@link String}
     * @return collection of user authorities of type {@link GrantedAuthority}
     * */
    default Collection<? extends GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
