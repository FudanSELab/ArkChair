package fudan.se.lab2.repository;

import fudan.se.lab2.domain.ReviewResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewResultRepository extends CrudRepository<ReviewResult, Long> {
    ReviewResult findReviewResultById(Long id);
}
