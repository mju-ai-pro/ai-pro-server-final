package teamproject.aipro.domain.role.controller;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamproject.aipro.domain.role.dto.request.RoleRequest;
import teamproject.aipro.domain.role.dto.response.RoleResponse;
import teamproject.aipro.domain.role.service.RoleService;

@RestController
@RequestMapping("/api/role")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@PostMapping("/set")
	public ResponseEntity<RoleResponse> setRole(Principal principal, @RequestBody RoleRequest roleRequest) {
		String userId = principal.getName();
		RoleResponse response = roleService.setRole(userId, roleRequest.getRole());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get")
	public ResponseEntity<String> getRole(Principal principal) {
		String userId = principal.getName();
		String role = roleService.getRole(userId);
		return ResponseEntity.ok(role);
	}
}
