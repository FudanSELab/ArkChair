package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Topic;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends CrudRepository<Topic, Long> {
    Topic findTopicByTopic(String topic);
}
