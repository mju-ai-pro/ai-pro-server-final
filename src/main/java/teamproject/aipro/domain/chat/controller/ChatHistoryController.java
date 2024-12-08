package teamproject.aipro.domain.chat.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamproject.aipro.domain.chat.dto.response.ChatCatalogResponse;
import teamproject.aipro.domain.chat.dto.response.ChatHistoryResponse;
import teamproject.aipro.domain.chat.service.ChatHistoryService;


@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/api")
public class ChatHistoryController {

	@Value("${jwt.secret}")
	private String secretKey;

	@Autowired
	private ChatHistoryService chatHistoryService;

	@GetMapping("/getChatHistory")
	public List<ChatHistoryResponse> getChatHistory(@RequestParam String catalogId) {
		return chatHistoryService.getChatHistory(catalogId);
	}

	@GetMapping("/getChatCatalog")
	public List<ChatCatalogResponse> getChatCatalog(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || authHeader.isEmpty()) {
			// Authorization 헤더가 없으면 빈 리스트 반환
			return new ArrayList<>();
		}
		// Bearer 토큰 파싱
		String jwtToken = authHeader.replace("Bearer ", "");
		//JWT에서 사용자 정보 추출
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes())
			.build()
			.parseClaimsJws(jwtToken)
			.getBody();
		String userId = claims.getSubject();
		return chatHistoryService.getChatCatalog(userId);
	}

}
