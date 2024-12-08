package teamproject.aipro.domain.chat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamproject.aipro.domain.chat.entity.ChatHistory;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
	List<ChatHistory> findByChatCatalog_Id(Long chatInventoryId);
}
