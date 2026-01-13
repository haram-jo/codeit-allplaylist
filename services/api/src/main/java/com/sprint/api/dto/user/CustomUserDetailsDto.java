package com.sprint.api.dto.user;

import com.sprint.api.entity.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
   PlaylistController에서 인증된 사용자 정보를 넘겨주기 위해
   만든 DTO
*/

@Getter
public class CustomUserDetailsDto implements UserDetails {

    private final UUID userId;      // 필요한 UUID
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // 엔티티를 받아서 DTO처럼 변환하여 들고 있음
    public CustomUserDetailsDto(User user) {
        this.userId = UUID.fromString(user.getId());
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // 시큐리티의 식별값은 이메일로 유지
    }
}
