package teamproject.aipro.domain.member.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import teamproject.aipro.domain.member.dto.request.LoginRequest;
import teamproject.aipro.domain.member.dto.request.SignupRequest;
import teamproject.aipro.domain.member.dto.response.MemberResponse;
import teamproject.aipro.domain.member.entity.Member;
import teamproject.aipro.domain.member.repository.MemberRepository;

@Service
public class MemberService {

	@Value("${jwt.secret}")
	private String secret;

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public MemberResponse signup(SignupRequest request) {
		Member member = new Member(request.getUserid(), passwordEncoder.encode(request.getPassword()),
			request.getUsername());
		memberRepository.save(member);
		return new MemberResponse(member.getId(), member.getUserid(), member.getUsername());
	}

	public String login(LoginRequest request) {
		Member member = memberRepository.findByuserid(request.getUserid())
			.orElse(null);
		if (member == null) {
			return "Invalid id or password";
		}
		if (passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
			return Jwts.builder()
				.setSubject(member.getUserid())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 864_000_00))
				.signWith(secretKey, SignatureAlgorithm.HS512)
				.compact();
		} else {
			return "Invalid id or password";
		}
	}

	public Member findByUserId(String userId) {
		return memberRepository.findByuserid(userId).orElse(null);
	}

	public boolean duplicateCheck(SignupRequest request) {
		if (findByUserId(request.getUserid()) == null) {
			return true;
		} else {
			return false;
		}
	}
}
