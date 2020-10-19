package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.HashSet;

public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    Conference findConferenceByFullName(String fullName);
    Conference findConferenceById(Long conferenceId);
    HashSet<Conference> findConferencesByStatus(String status);

    @Modifying
    @Query(value = "delete from Conference where id = ?1")
    void deleteConferenceById(Long conferenceId);
}
