package com.moadream.giftogether.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	//AuthenticationManager Bean 등록
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration
	configuration) throws Exception {
	return configuration.getAuthenticationManager();
	}
	
	// 정적리소스

	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
        		.requestMatchers(new AntPathRequestMatcher("/static/**"));
    }
	
	
	
	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http	.csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화
				.httpBasic(
						AbstractHttpConfigurer::disable) // 기본 폼 로그인 비활성화. JWT를 사용함
				.formLogin(AbstractHttpConfigurer::disable) // HTTP 기본 인증을 비활성화. JWT를 사용하여 인증 처리
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers("/", "/login/**" ,"/home").permitAll()
						.requestMatchers("/mypage/**",  "/member/**").hasAnyRole("MEMBER","ADMIN")
						//.anyRequest().authenticated())
						.anyRequest().permitAll())
				.formLogin(formLogin -> formLogin
						.loginPage("/login").permitAll()
						)
						
				.logout((logout) -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/home")
						.deleteCookies("JSESSIONID")
						.invalidateHttpSession(true)
						);
		return http.build();
	}

	
}
