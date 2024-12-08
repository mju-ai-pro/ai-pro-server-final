package teamproject.aipro.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatHistoryResponse {
	private String question;
	private String response;
}
