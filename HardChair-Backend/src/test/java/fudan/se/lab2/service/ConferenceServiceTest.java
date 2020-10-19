package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Rollback(false)//不回滚
class ConferenceServiceTest {

    private ConferenceRepository conferenceRepository;
    private User_ConferenceRepository userConferenceRepository;
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private MessageService messageService;
    private ConferenceService conferenceService;
    private PaperRepository paperRepository;
    private StatusOfInvitationRepository statusOfInvitationRepository;
    private TopicRepository topicRepository;
    private Conference conference;
    private AuthorRepository authorRepository;
    private ReviewResultRepository reviewResultRepository;
    private User userToSent;


    @Autowired
    public ConferenceServiceTest(ConferenceRepository conferenceRepository, User_ConferenceRepository userConferenceRepository, UserRepository userRepository, AuthorityRepository authorityRepository, MessageService messageService, PaperRepository paperRepository, StatusOfInvitationRepository statusOfInvitationRepository, TopicRepository topicRepository, AuthorRepository authorRepository, ReviewResultRepository reviewResultRepository) {
        this.conferenceRepository = conferenceRepository;
        this.userConferenceRepository = userConferenceRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.messageService = messageService;
        this.paperRepository = paperRepository;
        this.statusOfInvitationRepository = statusOfInvitationRepository;
        this.topicRepository = topicRepository;
        this.authorRepository = authorRepository;
        this.reviewResultRepository = reviewResultRepository;

        try {
            conferenceService
                    = new ConferenceService(
                    conferenceRepository,
                    userConferenceRepository,
                    userRepository,
                    authorityRepository,
                    messageService,
                    paperRepository,
                    statusOfInvitationRepository,
                    topicRepository,
                    authorRepository,
                    reviewResultRepository

            );
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

    }

    private void createDataForTest(){
        User user = new User("zitaoForConferenceInvitationStatus",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "zitaoForConferenceInvitationStatus",
                "USER");
        if(userRepository.findByUsername("zitaoForConferenceInvitationStatus")==null) {
            userRepository.save(user);
        }
        conference = new Conference("test",
                "testForConferenceInvitationStatus",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "zitaoForConferenceInvitationStatus",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("testForConferenceInvitationStatus")==null) {
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

        userToSent = new User("luqingyuanForConferenceInvitationStatus",
                "password",
                "tuanzi@126.com",
                "SH",
                "SH",
                "luqingyuanForConferenceInvitationStatus",
                "USER");
        if(userRepository.findByUsername("luqingyuanForConferenceInvitationStatus")==null) {
            userRepository.save(userToSent);
        }
    }

    @Test
    void createConference() {
        ConferenceRequest conferenceRequest = new ConferenceRequest();
        conferenceRequest.setNameAbbreviation("Conference");
        conferenceRequest.setFullName("conference demo");
        String[] time = {"2020-04-10 11:33:05.363","2020-04-10 11:33:05.363"};
        conferenceRequest.setTime(time);
        conferenceRequest.setLocation("FDU");
        conferenceRequest.setDeadline("2020-04-10 11:33:05.363");
        conferenceRequest.setResultAnnounceDate("2020-04-10 11:33:05.363");
        String[] topics = {"topic1", "topic2"};
        conferenceRequest.setTopic(topics);
        String username = "admin";

        assertTrue(conferenceService.createConference(conferenceRequest,username).equals(conferenceRepository.findConferenceByFullName("conference demo")));
    }

    @Test
    void searchByFullName() {
        User user = new User("zitao",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "xuanzitao",
                "USER");
        if(userRepository.findByUsername("zitao")==null){
            userRepository.save(user);
        }
        User userWithSameFullname = new User("luqingyuan",
                "password",
                "tuanzi@126.com",
                "SH",
                "SH",
                "xuanzitao",
                "USER");
        if(userRepository.findByUsername("luqingyuan")==null) {
            userRepository.save(userWithSameFullname);
        }

        Set<User> empty = new HashSet<>();
        Set<User> success = new HashSet<>();
        success.add(userRepository.findByUsername("zitao"));
        success.add(userRepository.findByUsername("luqingyuan"));
        assertEquals(empty, conferenceService.searchByFullName("notExist"));
        assertEquals(success, conferenceService.searchByFullName("xuanzitao"));
    }

    @Test
    void openSubmit() {
        Conference conference = new Conference("test",
                "testOpenSubmit",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "UNCHECKED",
                "zitao",
                "topic1");

        conferenceRepository.save(conference);
        conferenceService.openSubmit(conference.getId());
        assertEquals("SUBMIT_ALLOWED", conferenceRepository.findConferenceById(conference.getId()).getStatus());
        assertEquals("success", conferenceService.openSubmit(conference.getId()));
        assertEquals("conferenceNotFound", conferenceService.openSubmit((long) 2048));

    }

    @Test
    void findConference() {
        User user = new User("my_test",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "my_test",
                "USER");
        if(userRepository.findByUsername("my_test")==null) {
            userRepository.save(user);
        }
        Conference my_conference = new Conference("test",
                "my_test_conference",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "my_test",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("my_test_conference")==null) {
            conferenceRepository.save(my_conference);
        }
        Long conferenceId = my_conference.getId();
        System.out.println(conferenceId);
        assertTrue(conferenceService.findConference(conferenceId).equals(conferenceRepository.findConferenceById(conferenceId)));
    }

    @Test
    void findAuthorityOfUser() {
        User user = new User("zitaoForConferenceInvitationStatusFindAuthority",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "zitaoForConferenceInvitationStatusFindAuthority",
                "USER");
        if(userRepository.findByUsername("zitaoForConferenceInvitationStatusFindAuthority")==null) {
            userRepository.save(user);
        }
        conference = new Conference("test",
                "testForConferenceInvitationStatusFindAuthority",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "zitaoForConferenceInvitationStatusFindAuthority",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("testForConferenceInvitationStatusFindAuthority")==null) {
            conferenceRepository.save(conference);
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

        String username = "zitaoForConferenceInvitationStatusFindAuthority";
        Long conferenceId = conference.getId();
        Set<Authority> set = new HashSet<>();
        Authority authority2 = authorityRepository.findByAuthority("CHAIR");
        set.add(authority2);
        assertTrue(conferenceService.findAuthorityOfUser(username, conferenceId).equals(set));

        conferenceId = (long)1;
        set.clear();
        assertTrue(conferenceService.findAuthorityOfUser(username, conferenceId).equals(set));
    }

    @Test
    void checkChair() {
        Conference conference = new Conference("test",
                "testCheckChair",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "UNCHECKED",
                "zitao",
                "topic1");
        conferenceRepository.save(conference);

        assertEquals(true, conferenceService.checkChair("zitao", conference.getId()));
        assertEquals(false, conferenceService.checkChair("luqingyuan", conference.getId()));
    }

    @Test
    void updateAuthorityOfUserInConference() {
        User user = new User("zitao",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "xuanzitao",
                "USER");
        if(userRepository.findByUsername("zitao")==null){
            userRepository.save(user);
        }
        Conference conference = new Conference("test",
                "test",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "zitao",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("test")==null) {
            conferenceRepository.save(conference);
        }

        assertEquals("success", conferenceService.updateAuthorityOfUserInConference("zitao", "PC_MEMBER", conference.getId()));
        assertEquals("success", conferenceService.updateAuthorityOfUserInConference("zitao", "AUTHOR", conference.getId()));
        assertEquals("userNotFound", conferenceService.updateAuthorityOfUserInConference("notExist", "PC_MEMBER", conference.getId()));
        assertEquals("conferenceNotFound", conferenceService.updateAuthorityOfUserInConference("zitao", "PC_MEMBER", (long) 2048));
    }

    @Test
    @Transactional
    void verifyConference() {
        createDataForTest();
        Long conferenceId = conference.getId();
        String username = conference.getOwner();
        boolean isAllowed = true;

        conferenceService.verifyConference(conferenceId,username,isAllowed);
        assertEquals("CHECKED", conferenceRepository.findConferenceById(conferenceId).getStatus());

        // Create a new conference to avoid the conference demo(id=6) being deleted
        ConferenceRequest conferenceRequest = new ConferenceRequest();
        conferenceRequest.setNameAbbreviation("Conference");
        conferenceRequest.setFullName("conference demo");
        String[] time = {"2020-04-10 11:33:05.363","2020-04-10 11:33:05.363"};
        conferenceRequest.setTime(time);
        conferenceRequest.setLocation("FDU");
        conferenceRequest.setDeadline("2020-04-10 11:33:05.363");
        conferenceRequest.setResultAnnounceDate("2020-04-10 11:33:05.363");
        String[] topics = {"topic1", "topic2"};
        conferenceRequest.setTopic(topics);
        Conference conference_created = conferenceService.createConference(conferenceRequest,username);
        conferenceId = conference_created.getId();
        isAllowed = false;
        conferenceService.verifyConference(conferenceId, username, isAllowed);
        assertNull(conferenceRepository.findConferenceById(conferenceId));
    }

    @Test
    void getUncheckedConferences() {
        assertEquals(conferenceService.getUncheckedConferences(), conferenceRepository.findConferencesByStatus("UNCHECKED"));
    }

    @Test
    void getCheckedConferences() {
        assertEquals(conferenceService.getCheckedConferences(), conferenceRepository.findConferencesByStatus("CHECKED"));
    }

    @Test
    void getSubmitAllowedConferences() {
        assertEquals(conferenceService.getSubmitAllowedConferences(), conferenceRepository.findConferencesByStatus("SUBMIT_ALLOWED"));
    }

    @Test
    void getOpenReviewConferences() {
        assertEquals(conferenceService.getOpenReviewConferences(), conferenceRepository.findConferencesByStatus("OPEN_REVIEW"));
    }

    @Test
    void getOpenResultConferences() {
        assertEquals(conferenceService.getOpenResultConferences(), conferenceRepository.findConferencesByStatus("OPEN_RESULT"));
    }

    @Test
    void getOpenFinalResultConferences() {
        assertEquals(conferenceService.getOpenFinalResultConferences(), conferenceRepository.findConferencesByStatus("OPEN_FINAL_RESULT"));
    }

    @Test
    void deleteAuthor(){
        Set<Author> authors = new HashSet<>();
        assertEquals("success",conferenceService.deleteAuthors(authors));
    }

    @Test
    void findPapersByConferenceIdAndUserId(){
        User user = new User("zitaoForFindPapersByConferenceIdAndUserId",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "zitaoForFindPapersByConferenceIdAndUserId",
                "USER");

        Conference conference = new Conference("test",
                "testForFindPapersByConferenceIdAndUserId",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "zitaoForFindPapersByConferenceIdAndUserId2",
                "topic1");
        userRepository.save(user);
        conferenceRepository.save(conference);
        Set<Paper> papers = new HashSet<>();
        assertEquals(papers, conferenceService.findPapersByConferenceIdAndUserId(conference.getId(), user.getId()));

        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paperRepository.save(paper);
        papers.add(paper);
        assertEquals(papers,conferenceService.findPapersByConferenceIdAndUserId(conference.getId(),user.getId()));

    }

    @Test
    void findInvitationStatus(){

        User user = new User("zitaoForConferenceInvitationStatusFindInvitationStatus",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "zitaoForConferenceInvitationStatusFindInvitationStatus",
                "USER");
        if(userRepository.findByUsername("zitaoForConferenceInvitationStatusFindInvitationStatus")==null) {
            userRepository.save(user);
        }
        conference = new Conference("test",
                "testForConferenceInvitationStatusFindInvitationStatus",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "zitaoForConferenceInvitationStatusFindInvitationStatus",
                "topic1");
        if(conferenceRepository.findConferenceByFullName("testForConferenceInvitationStatusFindInvitationStatus")==null) {
            conferenceRepository.save(conference);
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
        userToSent = new User("luqingyuanForConferenceInvitationStatusFindInvitationStatus",
                "password",
                "tuanzi@126.com",
                "SH",
                "SH",
                "luqingyuanForConferenceInvitationStatusFindInvitationStatus",
                "USER");
        if(userRepository.findByUsername("luqingyuanForConferenceInvitationStatusFindInvitationStatus")==null) {
            userRepository.save(userToSent);
        }

        Long conferenceId = conference.getId();

        messageService.sendPCMemberInvitation("zitaoForConferenceInvitationStatusFindInvitationStatus", conferenceRepository.findConferenceByFullName("testForConferenceInvitationStatusFindInvitationStatus").getId(), userRepository.findByUsername("luqingyuanForConferenceInvitationStatusFindInvitationStatus").getId());
        StatusOfInvitation statusOfInvitation = new StatusOfInvitation();
        statusOfInvitation.setConferenceId(conferenceId);
        statusOfInvitation.setId((long) 1);
        statusOfInvitation.setStatus("UNREAD");
        statusOfInvitation.setUsername("luqingyuanForConferenceInvitationStatusFindInvitationStatus");
        statusOfInvitation.setUserToSentId(userToSent.getId());

        Set<StatusOfInvitation> statusOfInvitations = new HashSet<>();
        statusOfInvitations.add(statusOfInvitation);
        Set<User> users = new HashSet<>();
        User user1 = userRepository.findByUsername(statusOfInvitation.getUsername());
        user1.setPassword("UNREAD");
        users.add(user1);
        assertEquals(users.toString(),conferenceService.findInvitationStatus(conferenceId).toString());
    }

    @Test
    @Rollback(false)
    void addTopics(){
        User user = new User("testForAddTopics",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "testForAddTopics",
                "USER");
        userRepository.save(user);

        Conference conference = new Conference("test",
                "testForAddTopics",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "testForAddTopics",
                "topic1");
        conferenceRepository.save(conference);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setUserID(user.getId());
        userConferenceRepository.save(userAndConference);

        Long conferenceId = (long)1;
        String username = user.getUsername();
        String[] topics = new String[]{"topic"};

        assertEquals("user not found in conference", conferenceService.addTopics(conferenceId, username, topics));

        conferenceId = conference.getId();
        assertEquals("topic topic not found", conferenceService.addTopics(conferenceId, username, topics));

        topics = new String[]{"topic1"};
        assertEquals("success", conferenceService.addTopics(conferenceId, username, topics));
    }

    @Test
    void openReview(){
        Conference conference = new Conference("testForOpenReview",
                "testForOpenReview",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForReview0",
                "topic1");
        conferenceRepository.save(conference);

        int count = 0;
        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        while (count < 2){
            User user = new User("userForReview"+count,
                    "password",
                    "zitao@126.com",
                    "SH",
                    "SH",
                    "userForReview"+count,
                    "USER");

            user.setConferences(conferences);
            if(userRepository.findByUsername("userForReview"+count)==null) {
                userRepository.save(user);
            }

            UserAndConference userAndConference = new UserAndConference();
            userAndConference.setUserID(user.getId());
            userAndConference.setConferenceID(conference.getId());
            Set<Authority> authorities = new HashSet<>();
            Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
            authorities.add(authority);
            userAndConference.setAuthorities(authorities);
            userConferenceRepository.save(userAndConference);
            count++;
        }

        User owner = userRepository.findByUsername("userForReview0");

        assertEquals("open fail: number of PC MEMBER should more than 2", conferenceService.openReview(conference.getId()));

        User user2 = new User("userForReview2",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForReview2",
                "USER");

        user2.setConferences(conferences);
        if(userRepository.findByUsername("userForReview2")==null) {
            userRepository.save(user2);
            System.out.println(user2.getUsername());
        }


        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user2.getId());
        userAndConference.setConferenceID(conference.getId());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
        Authority authority2 = authorityRepository.findByAuthority("AUTHOR");
        authorities.add(authority);
        authorities.add(authority2);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        assertEquals("open fail: at least 1 paper is expected", conferenceService.openReview(conference.getId()));

        Set<Paper> papers = new HashSet<>();
        owner = userRepository.findByUsername("userForReview2");
        Paper paper = new Paper("title", "summary", conference.getId(), owner.getId(), "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic");
        paper.setStatus(0);
        Set<UserAndConference> userAndConferenceSet = new HashSet<>();
        userAndConferenceSet.add(userAndConference);
        paper.setUserAndConference(userAndConferenceSet);
        papers.add(paper);
        userAndConference.setPapers(papers);
        paperRepository.save(paper);
        userConferenceRepository.save(userAndConference);


        assertEquals("open success", conferenceService.openReview(conference.getId()));
    }

    @Test
    @Rollback(false)
    void findPCMEMBER(){
        User user = new User("testForFindPCMEMBER",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "testForFindPCMEMBER",
                "USER");
        userRepository.save(user);

        Conference conference = new Conference("testForFindPCMEMBER",
                "testForFindPCMEMBER",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "testForFindPCMEMBER",
                "topic1");
        conferenceRepository.save(conference);

        UserAndConference userAndConference = new UserAndConference(user.getId(), conference.getId());

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityRepository.findByAuthority("PC_MEMBER"));
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        LinkedList<User> users = new LinkedList<>();
        assertEquals(users, conferenceService.findPCMEMBER(conference.getId()));

    }

    @Test
    @Rollback(false)
    void distributeMethodOneSuccess(){
        Conference conference = new Conference("test",
                "testForMethodOne1",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "user1",
                "topic1");
        conferenceRepository.save(conference);

        int count = 0;
        Long userId = null;
        while (count < 3){
            User user = new User("userForMethodOne"+count,
                    "password",
                    "zitao@126.com",
                    "SH",
                    "SH",
                    "userForMethodOne"+count,
                    "USER");

            Set<Conference> conferences = new HashSet<>();
            conferences.add(conference);
            user.setConferences(conferences);
            userRepository.save(user);

            UserAndConference userAndConference = new UserAndConference();
            userAndConference.setUserID(user.getId());
            userAndConference.setConferenceID(conference.getId());
            Set<Authority> authorities = new HashSet<>();
            Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
            authorities.add(authority);
            userAndConference.setAuthorities(authorities);
            userAndConference.setTopics("topic2");
            userConferenceRepository.save(userAndConference);
            count++;
        }

        User user = new User("userForMethodOne3",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForMethodOne3",
                "USER");

        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setTopics("topic2");

        userId = user.getId();

        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title3", "summary", conference.getId(), userId, "pdf", "url", new Timestamp(200000));
        userAndConference.setTopics("topic1");
        paper.setTopics("topic1");
        paperRepository.save(paper);
        userAndConference.setPapers(papers);
        userConferenceRepository.save(userAndConference);

        assertEquals("success", conferenceService.distributeMethodOne(conference.getId()));
    }

    @Test
    @Rollback(false)
    void distributeMethodOneFailed(){
        Conference conference = new Conference("test",
                "testForMethodOne2",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "user1",
                "topic1");
        conferenceRepository.save(conference);

        int count = 4;
        Long userId = null;
        while (count < 7){
            User user = new User("userForMethodOne"+count,
                    "password",
                    "zitao@126.com",
                    "SH",
                    "SH",
                    "userForMethodOne"+count,
                    "USER");

            Set<Conference> conferences = new HashSet<>();
            conferences.add(conference);
            user.setConferences(conferences);
            userRepository.save(user);

            UserAndConference userAndConference = new UserAndConference();
            userAndConference.setUserID(user.getId());
            userAndConference.setConferenceID(conference.getId());
            Set<Authority> authorities = new HashSet<>();
            Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
            authorities.add(authority);
            userAndConference.setAuthorities(authorities);
            userAndConference.setTopics("topic2");

            if(count==4){
                userId = user.getId();

                Set<Paper> papers = new HashSet<>();
                Paper paper = new Paper("title", "summary", conference.getId(), userId, "pdf", "url", new Timestamp(200000));
                userAndConference.setTopics("topic1");
                paper.setTopics("topic1");
                paperRepository.save(paper);
                Paper paper2 = new Paper("title2", "summary2", conference.getId(), userId, "pdf", "url", new Timestamp(200000));
                paper2.setTopics("topic2");
                paperRepository.save(paper2);

                papers.add(paper2);
                userAndConference.setPapers(papers);
            }
            userConferenceRepository.save(userAndConference);
            count++;
        }
        assertEquals("open fail : no solution", conferenceService.distributeMethodOne(conference.getId()));
    }

    @Test
    @Rollback(false)
    void distributeMethodTwoSuccess(){
        Conference conference = new Conference("test",
                "testForMethodTwo1",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "user21",
                "topic1");
        conferenceRepository.save(conference);

        int count = 0;
        Long userId = null;
        while (count < 3){
            User user = new User("userForMethodTwo"+count,
                    "password",
                    "zitao@126.com",
                    "SH",
                    "SH",
                    "userForMethodTwo"+count,
                    "USER");

            Set<Conference> conferences = new HashSet<>();
            conferences.add(conference);
            user.setConferences(conferences);
            userRepository.save(user);

            UserAndConference userAndConference = new UserAndConference();
            userAndConference.setUserID(user.getId());
            userAndConference.setConferenceID(conference.getId());
            Set<Authority> authorities = new HashSet<>();
            Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
            authorities.add(authority);
            userAndConference.setAuthorities(authorities);
            userAndConference.setTopics("topic1");

            userConferenceRepository.save(userAndConference);
            count++;
        }

        User user = new User("userForMethodTwo3",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForMethodTwo3",
                "USER");

        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setTopics("topic1");

        userId = user.getId();
        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title3", "summary", conference.getId(), userId, "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic1");
        paperRepository.save(paper);
        papers.add(paper);
        userAndConference.setPapers(papers);
        userConferenceRepository.save(userAndConference);

        assertEquals("success", conferenceService.distributeMethodTwo(conference.getId()));
    }

    @Test
    @Rollback(false)
    void distributeMethodTwoFailed(){
        Conference conference = new Conference("test",
                "testForMethodTwo2",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "user21",
                "topic1");
        conferenceRepository.save(conference);

        int count = 4;
        Long userId = null;
        while (count < 8){
            User user = new User("userForMethodTwo"+count,
                    "password",
                    "zitao@126.com",
                    "SH",
                    "SH",
                    "userForMethodTwo"+count,
                    "USER");

            Set<Conference> conferences = new HashSet<>();
            conferences.add(conference);
            user.setConferences(conferences);
            userRepository.save(user);

            UserAndConference userAndConference = new UserAndConference();
            userAndConference.setUserID(user.getId());
            userAndConference.setConferenceID(conference.getId());
            Set<Authority> authorities = new HashSet<>();
            Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
            authorities.add(authority);
            userAndConference.setAuthorities(authorities);
            userAndConference.setTopics("topic1");

            if(count==7){
                userId = user.getId();
                Set<Paper> papers = new HashSet<>();
                Paper paper = new Paper("title", "summary", conference.getId(), userId, "pdf", "url", new Timestamp(200000));
                paper.setTopics("topic1");
                paperRepository.save(paper);
                Author author = new Author();
                author.setName("userForMethodTwo4");
                author.setEmail("zitao@126.com");
                author.setPaper(paper);
                authorRepository.save(author);
                papers.add(paper);
                userAndConference.setPapers(papers);
            }
            userConferenceRepository.save(userAndConference);
            count++;
        }

        assertEquals("open fail : no solution", conferenceService.distributeMethodTwo(conference.getId()));
    }

    @Test
    @Rollback(false)
    void reviewPapers(){
        User user = new User("userForReviewPapers",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForReviewPapers",
                "USER");
        userRepository.save(user);

        Conference conference = new Conference("testForReviewPapers",
                "testForReviewPapers",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForReviewPapers",
                "topic");
        conferenceRepository.save(conference);

        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic");
        paper.setStatus(0);
        paper.setUserId(user.getId());
        paper.setConferenceId(conference.getId());
        papers.add(paper);
        paperRepository.save(paper);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setPapers(papers);
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
        authorities.add(authority);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        assertEquals(papers.toString(), conferenceService.reviewPapers(user.getUsername(), conference.getId()).toString());
    }

    @Test
    @Rollback(false)
    void submitReviewResult(){
        User user = new User("userForReviewResult",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForReviewResult",
                "USER");
        userRepository.save(user);

        Conference conference = new Conference("testForReviewResult",
                "testForReviewResult",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForReviewResult",
                "topic");
        conferenceRepository.save(conference);

        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title4", "summary4", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic");
        paper.setStatus(0);
        papers.add(paper);
        paperRepository.save(paper);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setPapers(papers);
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
        authorities.add(authority);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        assertEquals("success", conferenceService.submitReviewResult(user.getUsername(),paper.getId(),2,"comment","confidence"));

    }

    @Test
    @Rollback(false)
    void openResult(){
        User user = new User("userForOpenResult",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForOpenResult",
                "USER");
        userRepository.save(user);

        Conference conference = new Conference("testForOpenResult",
                "testForOpenResult",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForOpenResult",
                "topic");
        conferenceRepository.save(conference);

        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic");
        paper.setStatus(0);
        paper.setConferenceId(conference.getId());
        papers.add(paper);
        paperRepository.save(paper);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setPapers(papers);
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
        authorities.add(authority);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        paper.setStatus(2);
        paperRepository.save(paper);
        assertEquals("open fail: wait for review", conferenceService.openResult(conference.getId()));
        paper.setStatus(3);
        paperRepository.save(paper);
        assertEquals("open success", conferenceService.openResult(conference.getId()));
    }

    @Test
    @Rollback(false)
    void reviewResults(){
        User user = new User("userForReviewResults",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForReviewResults",
                "USER");
        userRepository.save(user);

        Conference conference = new Conference("testForReviewResults",
                "testForReviewResults",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForReviewResults",
                "topic");
        conferenceRepository.save(conference);

        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic");
        paper.setStatus(0);
        paper.setUserId(user.getId());
        paper.setConferenceId(conference.getId());
        papers.add(paper);
        paperRepository.save(paper);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setPapers(papers);
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
        authorities.add(authority);
        authority = authorityRepository.findByAuthority("AUTHOR");
        authorities.add(authority);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);


        assertEquals(papers.toString(), conferenceService.reviewResults(user.getUsername(),conference.getId()).toString());
    }

    @Test
    @Rollback(false)
    void paperAuthority(){
        User user = new User("userForPaperAuthority",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForPaperAuthority",
                "USER");
        userRepository.save(user);

        User user2 = new User("userForPaperAuthority2",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForPaperAuthority2",
                "USER");
        userRepository.save(user2);

        Conference conference = new Conference("testForPaperAuthority",
                "testForPaperAuthority",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForPaperAuthority2",
                "topic");
        conferenceRepository.save(conference);

        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic");
        paper.setStatus(0);
        paper.setUserId(user.getId());
        paper.setConferenceId(conference.getId());
        papers.add(paper);
        paperRepository.save(paper);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findByAuthority("AUTHOR");
        authorities.add(authority);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);


        assertEquals("AUTHOR", conferenceService.paperAuthority(user.getUsername(),paper.getId()).getUrl());

        Authority authority2 = authorityRepository.findByAuthority("PC_MEMBER");
        userAndConference.setPapers(papers);
        authorities.add(authority2);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);
        assertEquals("PC_MEMBER", conferenceService.paperAuthority(user.getUsername(),paper.getId()).getUrl());

        authorities.remove(authority);
        userAndConference.setAuthorities(authorities);
        System.out.println(userAndConference.getAuthorities());
        userConferenceRepository.save(userAndConference);

        paper.setUserId(Long.parseLong("-1"));
        paperRepository.save(paper);
        assertEquals("PC_MEMBER", conferenceService.paperAuthority(user.getUsername(),paper.getId()).getUrl());


    }
}