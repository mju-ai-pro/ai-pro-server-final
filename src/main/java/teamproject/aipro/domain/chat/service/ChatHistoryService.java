package teamproject.aipro.domain.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import teamproject.aipro.domain.chat.dto.request.AiRequest;
import teamproject.aipro.domain.chat.dto.request.ChatRequest;
import teamproject.aipro.domain.chat.dto.response.ChatCatalogResponse;
import teamproject.aipro.domain.chat.dto.response.ChatHistoryResponse;
import teamproject.aipro.domain.chat.dto.response.ChatResponse;
import teamproject.aipro.domain.chat.entity.ChatCatalog;
import teamproject.aipro.domain.chat.entity.ChatHistory;
import teamproject.aipro.domain.chat.repository.ChatCatalogRepository;
import teamproject.aipro.domain.chat.repository.ChatHistoryRepository;

@Service
public class ChatHistoryService {
	@Autowired
	private ChatHistoryRepository chatHistoryRepository;
	@Autowired
	private ChatCatalogRepository chatCatalogRepository;
	@Value("${ai.summary}")
	private String uri;

	public ChatHistory saveChatHistory(String question, String response, String chatInvId) {
		ChatHistory chatHistory = new ChatHistory(question, response);
		chatCatalogRepository.findById(Long.parseLong(chatInvId)).ifPresent(chatHistory::setChatCatalog);
		return chatHistoryRepository.save(chatHistory);
	}

	public ChatCatalog saveChatCatalog(String userId, String summary) {
		ChatCatalog chatCatalog = new ChatCatalog(userId, summary);
		return chatCatalogRepository.save(chatCatalog);
	}

	public List<ChatHistoryResponse> getChatHistory(String chatCatalogId) {
		return chatHistoryRepository.findByChatCatalog_Id(Long.parseLong(chatCatalogId)).stream()
			.map(chatHistory -> new ChatHistoryResponse(
				chatHistory.getQuestion(),
				chatHistory.getResponse()))
			.collect(Collectors.toList());
	}

	public List<String> getChatHistoryAsStringList(String chatCatalogId) {
		return chatHistoryRepository.findByChatCatalog_Id(Long.parseLong(chatCatalogId)).stream()
			.map(chatHistory -> chatHistory.getQuestion() + " / " + chatHistory.getResponse())
			.collect(Collectors.toList());
	}

	public List<ChatCatalogResponse> getChatCatalog(String userId) {
		return chatCatalogRepository.findByUserId(userId).stream()
			.map(chatCatalog -> new ChatCatalogResponse(
				chatCatalog.getUserId(),
				chatCatalog.getId(),
				chatCatalog.getChatSummary()))
			.collect(Collectors.toList());
	}

	public ChatResponse summary(ChatRequest request) {
		RestTemplate restTemplate = new RestTemplate();
		String setRole = "";
		AiRequest aiRequest = new AiRequest();
		aiRequest.setUserId("a"); //수정
		aiRequest.setQuestion(request.getQuestion());
		aiRequest.setRole(setRole);
		aiRequest.setChatHistory(new ArrayList<>());
		try {
			String response = restTemplate.postForObject(uri, aiRequest, String.class);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);

			String message = rootNode.path("summary").asText();
			return new ChatResponse(message, "d"); //임시
		} catch (Exception e) {
			System.err.println("Error occurred while calling AI server: " + e.getMessage());
			return new ChatResponse("Error: Unable to get response from AI server.", "ㅇ");
		}
	}

}
