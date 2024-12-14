package teamproject.aipro.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import teamproject.aipro.domain.member.dto.request.LoginRequest;
import teamproject.aipro.domain.member.dto.request.SignupRequest;
import teamproject.aipro.domain.member.entity.Member;
import teamproject.aipro.domain.member.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ObjectMapper objectMapper; // JSON 직렬화를 위해 사용

	@Value("${jwt.secret}")
	private String secret;

	private String token; // 인증에 사용할 JWT 토큰

	@BeforeEach
	void setUp() {
		memberRepository.deleteAll();

		// 비밀번호 인코딩
		String encodedPassword = passwordEncoder.encode("password123");
		Member member = new Member("testUser", encodedPassword, "Tester");
		memberRepository.save(member);

		// JWT 토큰 생성
		SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		token = Jwts.builder()
			.setSubject(member.getUserid())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + 864_000_00))
			.signWith(secretKey, SignatureAlgorithm.HS512)
			.compact();
	}

	@Test
	@DisplayName("회원가입 성공 테스트")
	void testSignupSuccess() throws Exception {
		// Given
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUserid("newUser");
		signupRequest.setPassword("newPassword");
		signupRequest.setUsername("New User");

		// When & Then
		mockMvc.perform(post("/api/member/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userid").value("newUser"))
			.andExpect(jsonPath("$.username").value("New User"));
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	void testLoginSuccess() throws Exception {
		// Given
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserid("testUser");
		loginRequest.setPassword("password123");

		// When & Then
		mockMvc.perform(post("/api/member/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.notNullValue())); // 토큰 반환 확인
	}

	@Test
	@DisplayName("로그인 실패 테스트 - 잘못된 비밀번호")
	void testLoginFailure() throws Exception {
		// Given
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserid("testUser");
		loginRequest.setPassword("wrongPassword");

		// When & Then
		mockMvc.perform(post("/api/member/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string("Invalid id or password"));
	}

	@Test
	@DisplayName("아이디 중복 확인 테스트 - 중복 아님")
	void testDuplicateCheckNotExists() throws Exception {
		// Given
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUserid("uniqueUser");

		// When & Then
		mockMvc.perform(post("/api/member/duplicate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string("true")); // 중복 아님
	}

	@Test
	@DisplayName("아이디 중복 확인 테스트 - 중복됨")
	void testDuplicateCheckExists() throws Exception {
		// Given
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUserid("testUser");

		// When & Then
		mockMvc.perform(post("/api/member/duplicate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string("false")); // 중복됨
	}

}
