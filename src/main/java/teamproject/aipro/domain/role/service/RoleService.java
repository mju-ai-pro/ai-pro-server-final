package teamproject.aipro.domain.role.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import teamproject.aipro.domain.role.dto.response.RoleResponse;

@Service
public class RoleService {

	private final Map<String, String> userRoles = new HashMap<>();

	public RoleResponse setRole(String userId, String role) {
		userRoles.put(userId, role);
		return new RoleResponse(role);
	}

	public String getRole(String userId) {
		return userRoles.getOrDefault(userId, " ");
	}
}
