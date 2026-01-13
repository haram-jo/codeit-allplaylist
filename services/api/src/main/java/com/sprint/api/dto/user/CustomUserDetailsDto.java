package com.sprint.api.dto.user;

import com.sprint.api.entity.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/** 로그인한 유저 정보를 Spring Security가
 *  이해할 수 있는 형식으로 담아두는 DTO
 * - PlaylistController에서 인증된 사용자 정보를 넘겨줌
    */

@Getter
public class CustomUserDetailsDto implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private User user;

    public CustomUserDetailsDto(User user) {
        this.userId = UUID.fromString(user.getId());
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        this.user = user;
    }


    // ==== 스프링 시큐리티 규칙대로 유저 정보 받기 위해 필요한 것들 ==== //

    //유저 권한 알려줌
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
