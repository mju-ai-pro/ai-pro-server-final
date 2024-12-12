package teamproject.aipro.domain.role.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import teamproject.aipro.domain.member.entity.Member;
import teamproject.aipro.domain.member.repository.MemberRepository;
import teamproject.aipro.domain.role.dto.request.RoleRequest;
import teamproject.aipro.domain.role.service.RoleService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RoleControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RoleService roleService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${jwt.secret}")
	private String secret;

	private String token;

	@BeforeEach
	void setUp() {
		// 테스트 회원 생성 및 토큰 발급
		memberRepository.deleteAll();

		Member member = new Member("testUser", "password123", "Tester");
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
	@DisplayName("역할 설정 테스트 - 성공")
	void testSetRoleSuccess() throws Exception {
		// Given
		RoleRequest roleRequest = new RoleRequest();
		roleRequest.setUserid("testUser");
		roleRequest.setRole("ADMIN");

		// When & Then
		mockMvc.perform(post("/api/role/set")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.role").value("ADMIN"));
	}

	@Test
	@DisplayName("역할 가져오기 테스트 - 성공")
	void testGetRoleSuccess() throws Exception {
		// Given
		// 먼저 역할을 설정합니다.
		roleService.setRole("testUser", "USER");

		// When & Then
		mockMvc.perform(get("/api/role/get")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(content().string("USER"));
	}

}
