package teamproject.aipro.config.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final SecretKey secretKey;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String secret) {
		super(authenticationManager);
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		String path = request.getRequestURI();

		if (path.startsWith("/api/member") || path.startsWith("/api/test")) {
			chain.doFilter(request, response);
			return;
		}

		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.replace("Bearer ", "");
			try {
				Claims claims = Jwts.parserBuilder()
					.setSigningKey(secretKey)
					.build()
					.parseClaimsJws(token)
					.getBody();
				String username = claims.getSubject();
				if (username != null) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						username, null, new ArrayList<>());
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (ExpiredJwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("JWT token has expired.");
				return;
			} catch (SignatureException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Invalid JWT signature.");
				return;
			} catch (MalformedJwtException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("Malformed JWT token.");
				return;
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().write("Unexpected error while processing JWT token.");
				return;
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Authorization header is missing or does not start with 'Bearer '.");
			return;
		}
		chain.doFilter(request, response);
	}
}
