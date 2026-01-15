package com.sprint.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*  커스텀 로그인 Filter
    - 사용자가 보낸 이메일과 비밀번호를 가로채서 인증 처리
    - application/x-www-form-urlencoded 형식에서
    - 아이디와 비밀번호를 추출하여 인증 토큰 생성
    -
*/
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 로그인 요청은 반드시 POST 메서드
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // HttpServletRequest에서 아이디(username)와 비밀번호(password) 추출
        // 별도의 DTO (SignInRequest) 필요 없음
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) username = "";
        if (password == null) password = "";

        // 유저가 입력한 아이디와 비밀번호로 인증 신청서 토큰 생성
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username.trim(), password);

        // AuthenticationManager에게 신청서 제출, DB와 검증
        // 검증 성공시, LoginSuccessHandler로 이동하여 JWT 발급
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
