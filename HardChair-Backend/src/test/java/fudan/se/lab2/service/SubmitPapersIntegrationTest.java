package fudan.se.lab2.service;

import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class SubmitPapersIntegrationTest {

    private UserRepository userRepository;
    private ConferenceRepository conferenceRepository;
    private AuthorityRepository authorityRepository;
    private User_ConferenceRepository userConferenceRepository;
    private ConferenceService conferenceService;
    private MessageService messageService;
    private PaperRepository paperRepository;

    @Autowired
    public SubmitPapersIntegrationTest(UserRepository userRepository, ConferenceRepository conferenceRepository, AuthorityRepository authorityRepository, User_ConferenceRepository userConferenceRepository, ConferenceService conferenceService, MessageService messageService, PaperRepository paperRepository){
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.authorityRepository = authorityRepository;
        this.userConferenceRepository = userConferenceRepository;
        this.conferenceService = conferenceService;
        this.messageService = messageService;
        this.paperRepository = paperRepository;
    }

    @Test
    void openSubmitAndDoSubmitAndInvite(){
        //test open submit
        User user = new User("userForIntegratedTestOSADSAI",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForIntegratedTestOSADSAI",
                "USER");
        userRepository.save(user);

        Conference conference = new Conference("conferenceForIntegratedTestOSADSAI",
                "conferenceForIntegratedTestOSADSAI",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "CHECKED",
                "userForIntegratedTestOSADSAI",
                "topic1");
        conferenceRepository.save(conference);

        UserAndConference userAndConference = new UserAndConference(user.getId(), conference.getId());
        Authority authority1 = authorityRepository.findByAuthority("CHAIR");
        Authority authority2 = authorityRepository.findByAuthority("PC_MEMBER");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority1);
        authorities.add(authority2);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        assertEquals("success", conferenceService.openSubmit(conference.getId()));
        assertEquals("SUBMIT_ALLOWED", conferenceRepository.findConferenceById(conference.getId()).getStatus());

        //test submit paper and become author
        User userAuthor = new User("userAuthorForIntegratedTestOSADSAI",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userAuthorForIntegratedTestOSADSAI",
                "USER");
        userRepository.save(userAuthor);

        Paper paper = new Paper("paperForIntegratedTestOSADSAI", "summary", conference.getId(), userAuthor.getId(), "pdf", "url", new Timestamp(200000));
        paperRepository.save(paper);

        assertEquals("success", conferenceService.updateAuthorityOfUserInConference(userAuthor.getUsername(), "AUTHOR", conference.getId()));

        UserAndConference userAndConference1 = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(userAuthor.getId(), conference.getId());
        Set<Authority> authorities1 = new HashSet<>();
        Authority authority3 = authorityRepository.findByAuthority("AUTHOR");
        authorities1.add(authority3);

        assertEquals(authorities1, userAndConference1.getAuthorities());

        //test invite pc member
        User userPC = new User("userPCForIntegratedTestOSADSAI",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userPCForIntegratedTestOSADSAI",
                "USER");
        userRepository.save(userPC);

        assertEquals("success", messageService.sendPCMemberInvitation(user.getUsername(), conference.getId(), userPC.getId()));

        //test accept invitation and become PC member
        assertEquals("success", conferenceService.updateAuthorityOfUserInConference(userPC.getUsername(), "PC_MEMBER", conference.getId()));
        UserAndConference userAndConference2 = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(userPC.getId(), conference.getId());
        Set<Authority> authorities2 = new HashSet<>();
        authorities2.add(authority2);

        assertEquals(authorities2, userAndConference2.getAuthorities());
        assertEquals("success", messageService.sendPCMemberAcceptedOrRejectedMessage(conference.getId(), userPC.getUsername(), "PC_MEMBER_ACCEPTED"));
    }



}