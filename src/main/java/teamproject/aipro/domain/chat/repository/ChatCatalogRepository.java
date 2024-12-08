package teamproject.aipro.domain.chat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamproject.aipro.domain.chat.entity.ChatCatalog;

@Repository
public interface ChatCatalogRepository extends JpaRepository<ChatCatalog, Long> {
	List<ChatCatalog> findByUserId(String userId);
}
