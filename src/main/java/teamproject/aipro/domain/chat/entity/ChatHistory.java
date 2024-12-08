package teamproject.aipro.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "chat_Catalog_id")
	@JsonIgnoreProperties("chatHistories") // 이 필드를 JSON 직렬화에서 제외
	private ChatCatalog chatCatalog;

	@Column(name = "question", nullable = false, columnDefinition = "TEXT")
	private String question; // 질문 내용

	@Column(name = "response", columnDefinition = "TEXT")
	private String response; // 답변 내용

	public ChatHistory(String question, String response) {
		this.question = question;
		this.response = response;
	}

	public ChatCatalog getChatCatalog() {
		return chatCatalog;
	}

	public void setChatCatalog(ChatCatalog chatCatalog) {
		this.chatCatalog = chatCatalog;
	}

}
