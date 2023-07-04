package ru.clevertec.authservice.mapper;

import org.apache.commons.lang3.StringUtils;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import org.springframework.security.core.GrantedAuthority;
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
     * Mappings user authorities of type {@link GrantedAuthority} to authorities of type {@link String}
     *
     * @param grantedAuthorities collection of user authorities of type {@link GrantedAuthority}
     * @return list of user authorities of type {@link String}
     * */
    default List<String> mapToAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> StringUtils.prependIfMissing(authority, "ROLE_"))
                .toList();
    }
}
