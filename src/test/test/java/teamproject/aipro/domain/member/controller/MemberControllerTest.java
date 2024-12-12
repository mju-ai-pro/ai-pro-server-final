package teamproject.aipro.domain.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import teamproject.aipro.domain.member.dto.request.LoginRequest;
import teamproject.aipro.domain.member.dto.request.SignupRequest;
import teamproject.aipro.domain.member.dto.response.MemberResponse;
import teamproject.aipro.domain.member.entity.Member;
import teamproject.aipro.domain.member.service.MemberService;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testSignup() throws Exception {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUserid("testuser");
		signupRequest.setPassword("password123");

		Long id = 1L;
		String userid = "testuser";
		String username = "Test User";
		MemberResponse memberResponse = new MemberResponse(id, userid, username);

		when(memberService.signup(any(SignupRequest.class))).thenReturn(memberResponse);

		mockMvc.perform(post("/api/member/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userid").value("testuser"))
			.andExpect(jsonPath("$.username").value("Test User"));
	}

	@Test
	public void testLoginSuccess() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserid("testuser");
		loginRequest.setPassword("password123");

		String token = "mock-jwt-token";

		when(memberService.login(any(LoginRequest.class))).thenReturn(token);

		mockMvc.perform(post("/api/member/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string(token));
	}

	@Test
	public void testLoginFailure() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserid("invaliduser");
		loginRequest.setPassword("wrongpassword");

		when(memberService.login(any(LoginRequest.class))).thenReturn("Invalid id or password");

		mockMvc.perform(post("/api/member/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string("Invalid id or password"));
	}

	@Test
	public void testDuplicateCheck() throws Exception {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUserid("testuser");

		when(memberService.duplicateCheck(any(SignupRequest.class))).thenReturn(true);

		mockMvc.perform(post("/api/member/duplicate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}

	@Test
	public void testGetMemberInfo() throws Exception {
		Member member = new Member();
		member.setUserid("testuser");
		member.setUsername("Test User");

		when(memberService.findByUserId("testuser")).thenReturn(member);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/member/user")
				.principal(() -> "testuser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userid").value("testuser"))
			.andExpect(jsonPath("$.username").value("Test User"));
	}
}
