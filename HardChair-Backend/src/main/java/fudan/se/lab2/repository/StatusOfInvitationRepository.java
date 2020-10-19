package fudan.se.lab2.repository;

import fudan.se.lab2.domain.StatusOfInvitation;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface StatusOfInvitationRepository extends CrudRepository<StatusOfInvitation, Long> {
    StatusOfInvitation findStatusOfInvitationById(Long id);
    Set<StatusOfInvitation> findStatusOfInvitationsByConferenceId(Long conferenceId);
    StatusOfInvitation findStatusOfInvitationsByConferenceIdAndUserToSentId(Long conferenceId, Long userToSentId);
}
