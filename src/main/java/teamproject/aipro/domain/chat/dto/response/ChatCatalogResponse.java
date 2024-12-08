package teamproject.aipro.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatCatalogResponse {
	private String userId;
	private Long catalogId;
	private String summary;

}
