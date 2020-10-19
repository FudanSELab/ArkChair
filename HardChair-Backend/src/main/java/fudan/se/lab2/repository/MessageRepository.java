package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Message findMessageByMessageId(Long messageId);
}
