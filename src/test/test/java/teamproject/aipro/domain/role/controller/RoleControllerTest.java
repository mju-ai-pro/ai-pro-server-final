package teamproject.aipro.domain.role.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import teamproject.aipro.domain.role.dto.request.RoleRequest;
import teamproject.aipro.domain.role.dto.response.RoleResponse;
import teamproject.aipro.domain.role.service.RoleService;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoleService roleService;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testSetRole() throws Exception {
		String userId = "testuser";
		String role = "ADMIN";

		RoleRequest roleRequest = new RoleRequest();
		roleRequest.setUserid(userId);
		roleRequest.setRole(role);

		RoleResponse roleResponse = new RoleResponse(role);

		when(roleService.setRole(eq(userId), eq(role))).thenReturn(roleResponse);

		Principal mockPrincipal = () -> userId;

		mockMvc.perform(post("/api/role/set")
				.principal(mockPrincipal)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.role").value(role));
	}

	@Test
	public void testGetRole() throws Exception {
		String userId = "testuser";
		String role = "ADMIN";

		when(roleService.getRole(eq(userId))).thenReturn(role);

		Principal mockPrincipal = () -> userId;

		mockMvc.perform(get("/api/role/get")
				.principal(mockPrincipal))
			.andExpect(status().isOk())
			.andExpect(content().string(role));
	}
}
