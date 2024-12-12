package teamproject.aipro.domain.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import teamproject.aipro.domain.chat.dto.request.AiRequest;
import teamproject.aipro.domain.chat.dto.request.ChatRequest;
import teamproject.aipro.domain.chat.dto.response.ChatResponse;
import teamproject.aipro.domain.chat.entity.ChatCatalog;
import teamproject.aipro.domain.chat.exception.ChatException;
import teamproject.aipro.domain.chat.service.ChatHistoryService;
import teamproject.aipro.domain.chat.service.ChatService;
import teamproject.aipro.domain.role.service.RoleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

	private static final String TEST_AI_SERVER_URI = "http://test-ai-server/chat";

	@Mock
	private ChatHistoryService chatHistoryService;

	@Mock
	private RoleService roleService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private ChatService chatService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(chatService, "uri", TEST_AI_SERVER_URI);
	}

	@Test
	@DisplayName("유효한 Request - 채팅 성공")
	void whenQuestionWithValidRequest_thenSuccess() throws JsonProcessingException {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");
		String catalogId = "1";
		String userId = "testUser";

		String mockAiResponse = "{\"message\":\"Mocked AI response\"}";
		JsonNode mockRootNode = mock(JsonNode.class);
		JsonNode mockMessageNode = mock(JsonNode.class);

		when(roleService.getRole(userId)).thenReturn("testRole");
		when(chatHistoryService.getChatHistoryAsStringList(catalogId))
			.thenReturn(new ArrayList<>());

		when(restTemplate.postForObject(
			eq(TEST_AI_SERVER_URI),
			any(AiRequest.class),
			eq(String.class)
		)).thenReturn(mockAiResponse);

		when(objectMapper.readTree(mockAiResponse)).thenReturn(mockRootNode);
		when(mockRootNode.path("message")).thenReturn(mockMessageNode);
		when(mockMessageNode.asText()).thenReturn("Mocked AI response");

		ChatResponse response = chatService.question(chatRequest, catalogId, userId);

		assertNotNull(response);
		assertEquals("Mocked AI response", response.getMessage());
		assertEquals(catalogId, response.getCatalogId());

		verify(roleService).getRole(userId);
		verify(chatHistoryService).getChatHistoryAsStringList(catalogId);
		verify(chatHistoryService).saveChatHistory(
			chatRequest.getQuestion(),
			"Mocked AI response",
			catalogId
		);
	}

	@Test
	@DisplayName("Request가 Null - 예외 발생")
	void whenQuestionWithNullRequest_thenThrowException() {
		assertThrows(ChatException.class,
			() -> chatService.question(null, "1", "testUser"));
	}

	@Test
	@DisplayName("question필드에 값이 없음 - 예외 발생")
	void whenQuestionWithEmptyQuestion_thenThrowException() {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("");

		assertThrows(ChatException.class,
			() -> chatService.question(chatRequest, "1", "testUser"));
	}

	@Test
	@DisplayName("userId가 없음 - 예외 발생")
	void whenQuestionWithEmptyUserId_thenThrowException() {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");

		assertThrows(ChatException.class,
			() -> chatService.question(chatRequest, "1", ""));
	}

	@Test
	@DisplayName("AI 서버가 빈 응답을 보냄 - 예외 발생")
	void whenAIServerReturnsEmptyResponse_thenThrowException() {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");
		String catalogId = "1";
		String userId = "testUser";

		when(roleService.getRole(userId)).thenReturn("testRole");
		when(chatHistoryService.getChatHistoryAsStringList(catalogId))
			.thenReturn(new ArrayList<>());
		when(restTemplate.postForObject(anyString(), any(AiRequest.class), eq(String.class)))
			.thenReturn("");

		ChatException exception = assertThrows(ChatException.class,
			() -> chatService.question(chatRequest, catalogId, userId));

		assertEquals("Received empty response from AI server", exception.getMessage());
	}

	@Test
	@DisplayName("새로운 채팅방 만들어서 question 보내기 - 성공")
	void whenProcessNewCatalogRequestWithValidInput_thenSuccess() throws JsonProcessingException {
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setQuestion("Test question");
		String userId = "testUser";

		String mockAiResponse = "{\"message\":\"Mocked AI response for new catalog\"}";
		JsonNode mockRootNode = mock(JsonNode.class);
		JsonNode mockMessageNode = mock(JsonNode.class);

		ChatResponse summaryResponse = new ChatResponse("Summary", null);
		when(chatHistoryService.summary(chatRequest)).thenReturn(summaryResponse);

		ChatCatalog mockCatalog = new ChatCatalog(userId, "Summary");
		mockCatalog.setId(1L);
		when(chatHistoryService.saveChatCatalog(anyString(), anyString()))
			.thenReturn(mockCatalog);

		lenient().when(restTemplate.postForObject(
			eq(TEST_AI_SERVER_URI),
			any(AiRequest.class),
			eq(String.class)
		)).thenReturn(mockAiResponse);

		when(objectMapper.readTree(mockAiResponse)).thenReturn(mockRootNode);
		when(mockRootNode.path("message")).thenReturn(mockMessageNode);
		when(mockMessageNode.asText()).thenReturn("Mocked AI response for new catalog");

		ChatResponse response = chatService.processNewCatalogRequest(chatRequest, userId);

		assertNotNull(response);
		assertEquals("Mocked AI response for new catalog", response.getMessage());

		verify(chatHistoryService).summary(chatRequest);
		verify(chatHistoryService).saveChatCatalog(anyString(), anyString());
	}
}