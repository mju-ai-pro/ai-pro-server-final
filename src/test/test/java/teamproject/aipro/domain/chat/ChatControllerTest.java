package teamproject.aipro.domain.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import teamproject.aipro.domain.chat.controller.ChatController;
import teamproject.aipro.domain.chat.dto.request.ChatRequest;
import teamproject.aipro.domain.chat.dto.response.ChatResponse;
import teamproject.aipro.domain.chat.exception.ChatException;
import teamproject.aipro.domain.chat.service.ChatService;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatControllerTest {

	@Mock
	private ChatService chatService;

	@InjectMocks
	private ChatController chatController;

	private Principal mockPrincipal;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockPrincipal = new TestingAuthenticationToken("testUser", "password");
		SecurityContextHolder.getContext().setAuthentication((Authentication) mockPrincipal);
	}

	@Test
	@DisplayName("새로운 catalog로 채팅 - 성공")
	void whenQuestionWithNewCatalog_thenSuccess() {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");
		ChatResponse expectedResponse = new ChatResponse(
			"Response message", null);

		when(chatService.processNewCatalogRequest(chatRequest, "testUser"))
			.thenReturn(expectedResponse);

		ResponseEntity<ChatResponse> response = chatController.question(
			mockPrincipal, chatRequest, null);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	@DisplayName("기존 catalog로 채팅 이어가기 - 성공")
	void whenQuestionWithExistingCatalog_thenSuccess() {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");
		String catalogId = "123";
		ChatResponse expectedResponse = new ChatResponse("Response message", catalogId);

		when(chatService.processExistingCatalogRequest(chatRequest, catalogId, "testUser"))
			.thenReturn(expectedResponse);

		ResponseEntity<ChatResponse> response = chatController.question(mockPrincipal, chatRequest, catalogId);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	@DisplayName("Principal 객체 없음 - 예외 발생")
	void whenQuestionWithNullPrincipal_thenThrowsChatException() {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");

		assertThrows(ChatException.class, () -> {
			chatController.question(null, chatRequest, null);
		});
	}

	@Test
	@DisplayName("요청에 userId 없음 - 예외 발생")
	void whenQuestionWithEmptyUserId_thenThrowsChatException() {
		Principal emptyUserPrincipal = new TestingAuthenticationToken("", "password");
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");

		assertThrows(ChatException.class, () -> {
			chatController.question(emptyUserPrincipal, chatRequest, null);
		});
	}

	@Test
	@DisplayName("빈 question 값 - 예외 발생")
	void whenQuestionWithNullOrEmptyQuestion_thenThrowsChatException() {
		ChatRequest nullQuestionRequest = new ChatRequest();
		nullQuestionRequest.setQuestion(null);

		ChatRequest emptyQuestionRequest = new ChatRequest();
		emptyQuestionRequest.setQuestion("");

		assertThrows(ChatException.class, () -> {
			chatController.question(mockPrincipal, nullQuestionRequest, null);
		});

		assertThrows(ChatException.class, () -> {
			chatController.question(mockPrincipal, emptyQuestionRequest, null);
		});
	}

	@Test
	@DisplayName("런타임 에러 - 예외 발생")
	void whenQuestionWithServiceException_thenThrowsChatException() {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");

		when(chatService.processNewCatalogRequest(chatRequest, "testUser"))
			.thenThrow(new RuntimeException("Service error"));

		assertThrows(ChatException.class, () -> {
			chatController.question(mockPrincipal, chatRequest, null);
		});
	}
}
