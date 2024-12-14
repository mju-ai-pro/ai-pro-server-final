package teamproject.aipro.domain.chat.controller;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamproject.aipro.domain.chat.dto.request.ChatRequest;
import teamproject.aipro.domain.chat.dto.response.ChatResponse;
import teamproject.aipro.domain.chat.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/question")
	public ResponseEntity<ChatResponse> question(
		Principal principal,
		@RequestBody ChatRequest chatRequest,
		@RequestParam(required = false) String catalogId) {

		String userId = principal.getName();

		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		ChatResponse response = (catalogId == null || catalogId.trim().isEmpty())
			? chatService.processNewCatalogRequest(chatRequest, userId)
			: chatService.processExistingCatalogRequest(chatRequest, catalogId, userId);

		return ResponseEntity.ok(response);
	}
}
