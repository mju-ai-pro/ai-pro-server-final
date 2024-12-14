package teamproject.aipro.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import teamproject.aipro.config.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${jwt.secret}")
	private String secret;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(request -> {
				var config = new org.springframework.web.cors.CorsConfiguration();
				config.setAllowedOrigins(List.of("https://www.mjuaipro.site", "https://www.sinjjanggu.site", "https://www.hongkikii.site", "https://ai-pro-fe.vercel.app"));
				config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
				config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
				config.setAllowCredentials(true);
				return config;
			}))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(requests -> requests
				.anyRequest().permitAll())
			.addFilterBefore(jwtAuthenticationFilter(authenticationManager(null)),
				org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}



	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		return new JwtAuthenticationFilter(authenticationManager, secret);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
