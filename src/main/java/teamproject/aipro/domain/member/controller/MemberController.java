package teamproject.aipro.domain.member.controller;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamproject.aipro.domain.member.dto.request.LoginRequest;
import teamproject.aipro.domain.member.dto.request.SignupRequest;
import teamproject.aipro.domain.member.dto.response.MemberResponse;
import teamproject.aipro.domain.member.entity.Member;
import teamproject.aipro.domain.member.service.MemberService;

@RestController
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	public ResponseEntity<MemberResponse> signup(@RequestBody SignupRequest signupRequest) {
		MemberResponse response = memberService.signup(signupRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
		String token = memberService.login(loginRequest);
		if ("Invalid id or password".equals(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid id or password");
		}
		return ResponseEntity.ok(token);
	}

	@PostMapping("/duplicate")
	public boolean duplicateCheck(@RequestBody SignupRequest request) {
		return memberService.duplicateCheck(request);
	}

	@GetMapping("/jwttest")
	public String test() {
		return "test";
	}

	@GetMapping("/user")
	public Member getMemberInfo(Principal principal) {
		String userid = principal.getName();
		return memberService.findByUserId(userid);
	}
}
