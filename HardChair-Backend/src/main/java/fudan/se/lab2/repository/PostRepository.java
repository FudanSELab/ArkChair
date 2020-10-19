package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
    Post findPostById(Long id);
}
