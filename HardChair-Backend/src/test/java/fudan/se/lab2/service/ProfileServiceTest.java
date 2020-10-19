package fudan.se.lab2.service;

import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.domain.UserAndConference;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.repository.User_ConferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Rollback(false)//不回滚
class ProfileServiceTest {
    private ConferenceRepository conferenceRepository;
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private User_ConferenceRepository userConferenceRepository;
    private User user;
    private Conference conference;

    @Autowired
    public ProfileServiceTest(ConferenceRepository conferenceRepository, UserRepository userRepository, AuthorityRepository authorityRepository, User_ConferenceRepository userConferenceRepository){
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userConferenceRepository = userConferenceRepository;
    }

    private  void createDataForTest(){
        user = new User("zitaoForProfile",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "zitaoForProfile",
                "USER");
        if(userRepository.findByUsername("zitaoForProfile")==null) {
            userRepository.save(user);
        }
        conference = new Conference("test",
                "testForProfile",
                new Timestamp(200000),
                new Timestamp(200000),
                "SH",
                new Timestamp(200000),
                new Timestamp(200000),
                "test",
                "zitaoForProfile",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("testForProfile")==null) {
            conferenceRepository.save(conference);
        }else{
            return;//data have already created;
        }
        Set<Conference> conferences = user.getConferences();
        Iterator<Conference> conferenceIterator = conferences.iterator();
        boolean exist = false;
        while(conferenceIterator.hasNext()){
            if(conferenceIterator.next().getFullName().equals(conference.getFullName())){
                exist = true;
            }
        }
        if(!exist) {
            conferences.add(conference);
            user.setConferences(conferences);
            userRepository.save(user);
        }

        Authority authority = authorityRepository.findByAuthority("CHAIR");
        UserAndConference userAndConference = new UserAndConference(user.getId(), conference.getId());
        Set<Authority> authorities = userAndConference.getAuthorities();
        Iterator<Authority> authorityIterator = authorities.iterator();
        exist = false;
        while(authorityIterator.hasNext()){
            if(authorityIterator.next().getAuthority().equals(authority.getAuthority())){
                exist = true;
            }
        }
        if(!exist) {
            authorities.add(authority);
            userAndConference.setAuthorities(authorities);
            userConferenceRepository.save(userAndConference);
        }

    }

    @Test
    void findUserAndRelatedConference() {
        ProfileService profileService = new ProfileService(userRepository, userConferenceRepository);
        createDataForTest();

        ArrayList<Object> success = new ArrayList<>();
        ArrayList<Object> conferencesSet = new ArrayList<>();
        ArrayList<Object> conferenceSet = new ArrayList<>();
        success.add(user);
        ArrayList<Authority> authorities = new ArrayList<>();
        authorities.add(authorityRepository.findByAuthority("CHAIR"));
        conferenceSet.add(conference);
        conferenceSet.add(authorities);
        conferencesSet.add(conferenceSet);

        success.add(conferencesSet);

        ArrayList<Object> userNotFound = new ArrayList<>();
        userNotFound.add("message : userNotFound");


        assertEquals(Arrays.toString(success.toArray()), Arrays.toString(profileService.findUserAndRelatedConference("zitaoForProfile").toArray()));
        assertEquals(userNotFound, profileService.findUserAndRelatedConference("zitaozitao"));

    }
}