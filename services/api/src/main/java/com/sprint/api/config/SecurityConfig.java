package com.sprint.api.config;

import com.sprint.api.service.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/** 스프링 시큐리티 설정 클래스
  - HTTP 보안 설정 (세션 관리, CSRF 설정, 요청 권한 설정)
*/
@Configuration
@EnableWebSecurity // 스프링 시큐리티 설정 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    // PasswordEncoder 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정 (백엔드와 프론트 sse 무한로딩 해결용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 포트 허용
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // 쿠키 전송을 위해 필수 설정
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // CustomLoginFilter 빈 등록
    @Bean
    public CustomLoginFilter customLoginFilter() throws Exception {
        CustomLoginFilter filter = new CustomLoginFilter();
        filter.setFilterProcessesUrl("/api/auth/sign-in"); // Swagger 로그인 경로
        filter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler); // 로그인 성공 시 연결
        return filter;
    }

    // JWT 검증 필터 빈 등록 (로그인 성공 후 모든 요청에 대해 JWT 검증)
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, userDetailsService);
    }

    // SecurityFilterChain 빈 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 로컬 개발용 csrf
                .csrf(csrf -> csrf.disable())
                // SSE 경로
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT를 쓰므로 서버에 세션 만들지 않도록 (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 배포시 주석풀기 (쿠키 저장 방식)
                //csrf(csrf -> csrf
                //        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF 토큰을 쿠키에 저장
                //       .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()) // CSRF 핸들러 설정



                .authorizeHttpRequests(auth -> auth
                        // SSE 경로 추가
                        .requestMatchers("/api/sse/**").permitAll()
                        // 인증 없이 접근 가능한 POST 요청들 (회원가입, 로그인, 토큰 재발급)
                        .requestMatchers(HttpMethod.POST, "/api/users", "/api/auth/sign-in", "/api/auth/refresh").permitAll()
                        // 나머지는 모두 인증(로그인) 필요
                        .anyRequest().authenticated()
                );

                // CustomLoginFilter는 로그인 시도 시 작동
                http.addFilterAt(customLoginFilter(), UsernamePasswordAuthenticationFilter.class);

                // JwtAuthenticationFilter는 매 요청마다 토큰 검사 (로그인 필터 앞에 배치)
                http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

                return http.build();
            }
        }

        /* 테스트 코드
                .csrf(csrf -> csrf.disable()) // 테스트 위해 임시허용
                // 모든 경로에 대해 접근 허용
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        http.addFilterBefore(customLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        */
