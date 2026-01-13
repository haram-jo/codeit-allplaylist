package com.sprint.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/** CustomLoginFilter는 로그인시 토큰을 주지만,
 * - 이후 요청이 올때 헤더의 토큰을 해석해서 누구인지
 * - 알아내는 Filter가 필요
*/

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService; // 사용자 정보를 DB에서 로드하는 서비스

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 토큰값만 뽑음
        String token = resolveToken(request);

        // 로그 추가: 요청이 들어올 때마다 토큰 존재 여부 확인
        System.out.println("Incoming Request URL: " + request.getRequestURI());
        System.out.println("Resolved Token: " + token);

        if (token != null && jwtProvider.validateToken(token)) {

            String email = jwtProvider.getEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            // SecurityContext에 인증 정보 저장, 이후 컨트롤러에서 인증 정보 사용 가능
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 로그 추가: 인증 성공 확인
            System.out.println("Authentication Success: " + email);

        }
        filterChain.doFilter(request, response);
    }

    // 순수 JWT 토큰 문자열만 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
