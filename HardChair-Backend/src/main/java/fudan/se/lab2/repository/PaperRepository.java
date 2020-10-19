package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Paper;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PaperRepository extends CrudRepository<Paper, Long> {
    Paper findPaperById(Long id);
    Set<Paper> findPapersByConferenceIdAndUserId(Long conferenceId, Long userId);
    Set<Paper> findPapersByConferenceId(Long conferenceId);
}
