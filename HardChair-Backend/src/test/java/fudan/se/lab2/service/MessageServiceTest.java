package fudan.se.lab2.service;

import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Rollback(false)//不回滚
class MessageServiceTest {

    private ConferenceRepository conferenceRepository;
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private AuthorityRepository authorityRepository;
    private User_ConferenceRepository userConferenceRepository;
    private StatusOfInvitationRepository statusOfInvitationRepository;
    private MessageService messageService;

    @Autowired
    public MessageServiceTest(ConferenceRepository conferenceRepository, UserRepository userRepository, MessageRepository messageRepository,AuthorityRepository authorityRepository, User_ConferenceRepository userConferenceRepository, StatusOfInvitationRepository statusOfInvitationRepository){
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.authorityRepository = authorityRepository;
        this.userConferenceRepository = userConferenceRepository;
        this.statusOfInvitationRepository = statusOfInvitationRepository;
        messageService = new MessageService(conferenceRepository, userRepository, messageRepository,statusOfInvitationRepository);
    }

    private void createDataForTest(){
        User user = new User("zitaoForMessage",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "zitaoForMessage",
                "USER");
        if(userRepository.findByUsername("zitaoForMessage")==null) {
            userRepository.save(user);
        }
        Conference conference = new Conference("test",
                "testForMessage",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "zitaoForMessage",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("testForMessage")==null) {
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

        User userToSent = new User("luqingyuanForMessage",
                "password",
                "tuanzi@126.com",
                "SH",
                "SH",
                "luqingyuanForMessage",
                "USER");
        if(userRepository.findByUsername("luqingyuanForMessage")==null) {
            userRepository.save(userToSent);
        }
    }
    @Test
    void sendPCMemberInvitation() {
        createDataForTest();
        assertEquals("success", messageService.sendPCMemberInvitation("zitaoForMessage", conferenceRepository.findConferenceByFullName("testForMessage").getId(), userRepository.findByUsername("luqingyuanForMessage").getId() ));
        assertEquals("conferenceNotFound", messageService.sendPCMemberInvitation("zitaoForMessage", (long)2048, userRepository.findByUsername("luqingyuanForMessage").getId() ));
        assertEquals("userNotFound", messageService.sendPCMemberInvitation("zitaoForMessage", conferenceRepository.findConferenceByFullName("testForMessage").getId(), (long)2048 ));
    }

    @Test
    void sendConferenceCheckedOrAbolishedMessage() {
        createDataForTest();
        assertEquals("success", messageService.sendConferenceCheckedOrAbolishedMessage(conferenceRepository.findConferenceByFullName("testForMessage").getId(), userRepository.findByUsername("zitaoForMessage").getId(), "CONFERENCE_CHECKED"));
        assertEquals("success", messageService.sendConferenceCheckedOrAbolishedMessage(conferenceRepository.findConferenceByFullName("testForMessage").getId(), userRepository.findByUsername("zitaoForMessage").getId(), "CONFERENCE_ABOLISHED"));
        assertEquals("conferenceNotFound", messageService.sendConferenceCheckedOrAbolishedMessage((long) 2048, userRepository.findByUsername("zitaoForMessage").getId(), "CONFERENCE_CHECKED"));
        assertEquals("userNotFound", messageService.sendConferenceCheckedOrAbolishedMessage(conferenceRepository.findConferenceByFullName("testForMessage").getId(), (long) 2048, "CONFERENCE_CHECKED"));
    }

    @Test
    void sendPCMemberAcceptedOrRejectedMessage() {
        createDataForTest();
        Conference conferenceWithOwnerNotExist = new Conference("conferenceWithOwnerNotExist",
                "conferenceWithOwnerNotExist",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "OwnerNotExist",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("conferenceWithOwnerNotExist")==null) {
            conferenceRepository.save(conferenceWithOwnerNotExist);
        }

        assertEquals("success", messageService.sendPCMemberAcceptedOrRejectedMessage(conferenceRepository.findConferenceByFullName("testForMessage").getId(), "luqingyuanForMessage", "PC_MEMBER_ACCEPTED"));
        assertEquals("success", messageService.sendPCMemberAcceptedOrRejectedMessage(conferenceRepository.findConferenceByFullName("testForMessage").getId(), "luqingyuanForMessage", "PC_MEMBER_REJECTED"));
        assertEquals("conferenceNotFound", messageService.sendPCMemberAcceptedOrRejectedMessage((long) 2048, "luqingyuanForMessage", "PC_MEMBER_ACCEPTED"));
        assertEquals("userNotFound", messageService.sendPCMemberAcceptedOrRejectedMessage(conferenceRepository.findConferenceByFullName("testForMessage").getId(), "tuanzi", "PC_MEMBER_ACCEPTED" ));
        assertEquals("ownerNotFound", messageService.sendPCMemberAcceptedOrRejectedMessage(conferenceWithOwnerNotExist.getId(), "luqingyuanForMessage", "PC_MEMBER_ACCEPTED" ));
    }

    @Test
    void getAllMessagesOfUser(){
        createDataForTest();
        assertEquals(userRepository.findByUsername("zitaoForMessage").getMessages(), messageService.getAllMessagesOfUser("zitaoForMessage"));
        assertEquals(userRepository.findByUsername("luqingyuanForMessage").getMessages(), messageService.getAllMessagesOfUser("luqingyuanForMessage"));
    }

    @Test
    void updateStatus() {
        createDataForTest();
        long messageId = 1;
        assertEquals("messageNotFound", messageService.updateStatus(messageId));

        messageService.sendPCMemberInvitation("zitaoForMessage", conferenceRepository.findConferenceByFullName("testForMessage").getId(), userRepository.findByUsername("luqingyuanForMessage").getId());
        messageId = 12;
//        assertEquals("success", messageService.updateStatus(messageId));
    }
}