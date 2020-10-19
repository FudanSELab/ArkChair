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
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Rollback(false)
class ReviewPapersIntegrationTest {

    private ConferenceRepository conferenceRepository;
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private User_ConferenceRepository userConferenceRepository;
    private PaperRepository paperRepository;
    private ReviewResultRepository reviewResultRepository;

    private ConferenceService conferenceService;

    @Autowired
    public ReviewPapersIntegrationTest(ConferenceRepository conferenceRepository, UserRepository userRepository, AuthorityRepository authorityRepository, User_ConferenceRepository userConferenceRepository, PaperRepository paperRepository, ReviewResultRepository reviewResultRepository, ConferenceService conferenceService){
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userConferenceRepository = userConferenceRepository;
        this.paperRepository = paperRepository;
        this.reviewResultRepository = reviewResultRepository;
        this.conferenceService = conferenceService;
    }

    @Test
    void reviewPapersTest() {
        Conference conference = new Conference("testForReviewPapers",
                "testForReviewPapers",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForReviewPapers0",
                "topic1");
        conferenceRepository.save(conference);

        int count = 0;
        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);

        while (count < 2){
            User user = new User("userForReviewPapers"+count,
                    "password",
                    "zitao@126.com",
                    "SH",
                    "SH",
                    "userForReviewPapers"+count,
                    "USER");

            user.setConferences(conferences);
            if(userRepository.findByUsername("userForReviewPapers"+count)==null) {
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

        assertEquals("open fail: number of PC MEMBER should more than 2", conferenceService.openReview(conference.getId()));

        User user2 = new User("userForReviewPapers2",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForReviewPapers2",
                "USER");

        user2.setConferences(conferences);
        if(userRepository.findByUsername("userForReviewPapers2")==null) {
            userRepository.save(user2);
        }

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user2.getId());
        userAndConference.setConferenceID(conference.getId());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findByAuthority("PC_MEMBER");
        authorities.add(authority);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        assertEquals("open fail: at least 1 paper is expected", conferenceService.openReview(conference.getId()));

        User user3 = new User("userForReviewPapers3",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForReviewPapers3",
                "USER");

        user3.setConferences(conferences);
        if(userRepository.findByUsername("userForReviewPapers3")==null) {
            userRepository.save(user3);
        }

        Set<Paper> papers = new HashSet<>();
        Paper paper = new Paper("title", "summary", conference.getId(), user3.getId(), "pdf", "url", new Timestamp(200000));
        paper.setTopics("topic");
        paper.setStatus(0);
        paper.setUserId(user3.getId());
        paper.setConferenceId(conference.getId());
        papers.add(paper);
        paperRepository.save(paper);

        UserAndConference userAndConference3 = new UserAndConference();
        userAndConference3.setUserID(user3.getId());
        userAndConference3.setConferenceID(conference.getId());
        userAndConference3.setPapers(papers);
        Set<Authority> authorities3 = new HashSet<>();
        Authority authority3 = authorityRepository.findByAuthority("PC_MEMBER");
        authorities3.add(authority3);
        userAndConference3.setAuthorities(authorities3);
        userConferenceRepository.save(userAndConference3);

        assertEquals("open success", conferenceService.openReview(conference.getId()));

        distributePapers(conference);

        reviewPapers(conference, user3, papers);

        openResult(conference, paper);
    }

    private void distributePapers(Conference conference){
        assertEquals("success", conferenceService.distributeMethodTwo(conference.getId()));
    }

    private void reviewPapers(Conference conference, User user, Set<Paper> papers) {
        assertEquals(papers.toString(), conferenceService.reviewPapers(user.getUsername(), conference.getId()).toString());
    }

    private void openResult(Conference conference, Paper paper) {
        paper.setStatus(2);
        paperRepository.save(paper);
        assertEquals("open fail: wait for review", conferenceService.openResult(conference.getId()));
        paper.setStatus(3);
        Set<ReviewResult> reviewResults = new HashSet<>();
        ReviewResult reviewResult = new ReviewResult();
        reviewResult.setConfirm(0);
        reviewResult.setPaper(paper);
        reviewResultRepository.save(reviewResult);
        paper.setReviewResults(reviewResults);
        paperRepository.save(paper);
        assertEquals("wait for all review results to be confirmed or revised!", conferenceService.openResult(conference.getId()));
        reviewResult.setConfirm(1);
        reviewResultRepository.save(reviewResult);
        assertEquals("open success", conferenceService.openResult(conference.getId()));
    }
}
