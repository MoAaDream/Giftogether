package com.moadream.giftogether.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  
	@Autowired
	private UserDetailsService userdetailsService;
	
    // 정적리소스 필터 해제 
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

 // 세션 만료 시 리다이렉션 설정
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    
    
    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 폼 로그인 비활성화. 
                .formLogin(AbstractHttpConfigurer::disable) // HTTP 기본 인증을 비활성화. 
               
                .authorizeHttpRequests((authorize) -> authorize
                		.requestMatchers("/admin/**").hasRole("ADMIN") // /admin/** 경로는 ADMIN 권한을 가진 사용자만 접근 허용
                        .requestMatchers("/", "/login/**", "/home", "/current-user").permitAll()
                        .requestMatchers("/member/**").authenticated() // /member/** 경로는 인증된 사용자에게만 접근 허용
                        .anyRequest().permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout-success")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement(session -> session
                        .sessionFixation().none() // 세션 고정 방지
                        .invalidSessionUrl("/login")); // 세션이 유효하지 않을 때 리다이렉션할 URL;

        return http.build();
    }
}