package ru.clevertec.newsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorityMapper {

    default Collection<? extends GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
