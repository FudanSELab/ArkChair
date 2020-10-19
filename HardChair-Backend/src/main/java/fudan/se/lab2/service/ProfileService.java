package fudan.se.lab2.service;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.domain.UserAndConference;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.repository.User_ConferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProfileService {
    private UserRepository userRepository;
    private User_ConferenceRepository userConferenceRepository;

    @Autowired
    public ProfileService( UserRepository userRepository, User_ConferenceRepository userConferenceRepository){
        this.userRepository = userRepository;
        this.userConferenceRepository = userConferenceRepository;
    }

    public List<Object> findUserAndRelatedConference(String username){
        ArrayList<Object> profile = new ArrayList<>();
        User user = userRepository.findByUsername(username);
        if(user==null){
            profile.add("message : userNotFound");
            return profile;
        }
        profile.add(user);

        ArrayList<Object> conferenceSet;
        ArrayList<Object> conferencesSet = new ArrayList<>();
        Set<Conference> conferences = user.getConferences();

        for (Conference conference : conferences) {
            conferenceSet = new ArrayList<>();
            conferenceSet.add(conference);
            UserAndConference userAndConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), conference.getId());
            conferenceSet.add(userAndConference.getAuthorities());
            conferencesSet.add(conferenceSet);
        }

        profile.add(conferencesSet);
        return profile;
    }
}
