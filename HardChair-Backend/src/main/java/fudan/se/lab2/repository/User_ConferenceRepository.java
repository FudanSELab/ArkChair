package fudan.se.lab2.repository;

import fudan.se.lab2.domain.UserAndConference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_ConferenceRepository extends CrudRepository<UserAndConference, Long> {
    UserAndConference findUserConferenceByUserIDAndConferenceID(Long userID, Long conferenceID);
}
