package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class ApplicationIntegrationTest {

    private UserRepository userRepository;
    private ConferenceService conferenceService;
    private ConferenceRepository conferenceRepository;

    @Autowired
    public ApplicationIntegrationTest(UserRepository userRepository, ConferenceService conferenceService, ConferenceRepository conferenceRepository){
        this.userRepository = userRepository;
        this.conferenceService = conferenceService;
        this.conferenceRepository = conferenceRepository;
    }

    @Test
    void conferenceApplicationAndVerification(){
        User user = new User("userForIntegratedTestCAAV",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForIntegratedTestCAAV",
                "USER");
        userRepository.save(user);


        ConferenceRequest conferenceRequest = new ConferenceRequest();
        conferenceRequest.setNameAbbreviation("conferenceForIntegratedTest");
        conferenceRequest.setFullName("conferenceForIntegratedTest");
        String[] time = {"2020-04-10 11:33:05.363","2020-04-10 11:33:05.363"};
        conferenceRequest.setTime(time);
        conferenceRequest.setLocation("FDU");
        conferenceRequest.setDeadline("2020-04-10 11:33:05.363");
        conferenceRequest.setResultAnnounceDate("2020-04-10 11:33:05.363");
        String[] topics = {"topic1", "topic2"};
        conferenceRequest.setTopic(topics);
        String username = "userForIntegratedTestCAAV";

        //test application
        assertTrue(conferenceService.createConference(conferenceRequest,username).equals(conferenceRepository.findConferenceByFullName("conferenceForIntegratedTest")));

        Long conferenceId = conferenceRepository.findConferenceByFullName("conferenceForIntegratedTest").getId();

        //test verification
        conferenceService.verifyConference(conferenceId,username, true);
        assertEquals("CHECKED", conferenceRepository.findConferenceById(conferenceId).getStatus());
    }

}