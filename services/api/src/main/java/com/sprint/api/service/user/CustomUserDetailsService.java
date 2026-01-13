package com.sprint.api.service.user;

import com.sprint.api.dto.user.CustomUserDetailsDto;
import com.sprint.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** 시큐리티와 DB 사이를 이어주는 클래스
  * -시큐리티가 이메일 찾아줘라고 하면,
  * -DB에 가서 데이터를 꺼내온 뒤,
  * -시큐리티가 이해할 수 있는 규격(UserDetails)에 담아서 전달
*/

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String idOrEmail) throws UsernameNotFoundException {
        // 1. 먼저 ID(UUID), 2. 없으면 이메일로 사용자 찾기
        return userRepository.findById(idOrEmail)
                .map(CustomUserDetailsDto::new)
                .orElseGet(() -> userRepository.findByEmail(idOrEmail)
                        .map(CustomUserDetailsDto::new)
                        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + idOrEmail)));
    }
}
