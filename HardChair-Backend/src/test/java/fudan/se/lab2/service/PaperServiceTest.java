package fudan.se.lab2.service;

import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import javafx.geometry.Pos;
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
class PaperServiceTest {

    private UserRepository userRepository;
    private PaperService paperService;
    private PostRepository postRepository;
    private PaperRepository paperRepository;
    private ConferenceService conferenceService;
    private ConferenceRepository conferenceRepository;
    private ReviewResultRepository reviewResultRepository;
    private User_ConferenceRepository userConferenceRepository;

    private static final String NO_AUTHORITY = "No Authority";
    private static final String SUCCESS = "success";

    @Autowired
    public PaperServiceTest(PostRepository postRepository, PaperRepository paperRepository, ConferenceService conferenceService, UserRepository userRepository, ConferenceRepository conferenceRepository, ReviewResultRepository reviewResultRepository, User_ConferenceRepository userConferenceRepository){
        this.postRepository = postRepository;
        this.paperRepository = paperRepository;
        this.conferenceService = conferenceService;
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.reviewResultRepository = reviewResultRepository;
        this.userConferenceRepository = userConferenceRepository;

        paperService = new PaperService(
                postRepository,
                paperRepository,
                conferenceService,
                userRepository,
                conferenceRepository,reviewResultRepository
        );
    }


    @Test
    @Rollback(false)
    void submitPost(){
        Conference conference = new Conference("conferenceForSubmitPost",
                "conferenceForSubmitPost",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForSubmitPostOwner",
                "topic");
        conferenceRepository.save(conference);

        User user = new User("userForSubmitPost",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForSubmitPost",
                "USER");

        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setTopics("topic");
        userConferenceRepository.save(userAndConference);

        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paperRepository.save(paper);
        Set<Paper> papers = new HashSet<>();
        userAndConference.setPapers(papers);
        userConferenceRepository.save(userAndConference);
        assertEquals("fail: conference status", paperService.submitPost("fail: case1", "userForSubmitPost", paper.getId(), (long)3));

        conference.setStatus("OPEN_REVIEW");
        conferenceRepository.save(conference);
        assertEquals(NO_AUTHORITY, paperService.submitPost("fail: case2", "userForSubmitPost", paper.getId(), (long)3));

        conference.setOwner("userForSubmitPost");
        conferenceRepository.save(conference);
        assertEquals(SUCCESS, paperService.submitPost("success", "userForSubmitPost", paper.getId(), (long)3));
    }

    @Test
    @Rollback(false)
    void submitRebuttal(){
        Conference conference = new Conference("testForSubmitRebuttal",
                "testForSubmitRebuttal",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForSubmitRebuttal",
                "topic");
        conferenceRepository.save(conference);

        User user = new User("userForSubmitRebuttal",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForSubmitRebuttal",
                "USER");

        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setTopics("topic1");
        userConferenceRepository.save(userAndConference);

        Paper paper = new Paper("title", "summary", conference.getId(), (long)0, "pdf", "url", new Timestamp(200000));
        paperRepository.save(paper);
        Set<Paper> papers = new HashSet<>();
        userAndConference.setPapers(papers);
        userConferenceRepository.save(userAndConference);
        assertEquals(NO_AUTHORITY, paperService.submitRebuttal("no authority: case1", "userForSubmitRebuttal", paper.getId()));

        paper.setUserId(user.getId());
        paperRepository.save(paper);
        assertEquals(NO_AUTHORITY, paperService.submitRebuttal("no authority: case2", "userForSubmitRebuttal", paper.getId()));

        conference.setStatus("OPEN_RESULT");
        conferenceRepository.save(conference);
        paper.setRebuttal("The rebuttal has been submitted");
        paperRepository.save(paper);
        assertEquals("you have already submitted the rebuttal!", paperService.submitRebuttal("no authority: case2", "userForSubmitRebuttal", paper.getId()));

        paper.setRebuttal(null);
        paperRepository.save(paper);
        assertEquals(SUCCESS, paperService.submitRebuttal("no authority: case2", "userForSubmitRebuttal", paper.getId()));
    }

    @Test
    @Rollback(false)
    void reviseOrConfirmReviewResult(){
        Conference conference = new Conference("testForReviseOrConfirmReviewResult",
                "testForReviseOrConfirmReviewResult",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForReviseOrConfirmReviewResult",
                "topic");
        conferenceRepository.save(conference);

        User user = new User("userForReviseOrConfirmReviewResult",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForReviseOrConfirmReviewResult",
                "USER");

        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setTopics("topic");
        userConferenceRepository.save(userAndConference);

        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        Set<Post> posts = new HashSet<>();
        Post post = new Post();
        post.setStatus(1);
        post.setPaper(paper);
        posts.add(post);
        paper.setPosts(posts);
        paperRepository.save(paper);
        Set<Paper> papers = new HashSet<>();
        userAndConference.setPapers(papers);
        userConferenceRepository.save(userAndConference);

        Set<ReviewResult> reviewResults = new HashSet<>();
        ReviewResult reviewResult = new ReviewResult();
        reviewResult.setPaper(paper);
        reviewResult.setIdOfPcMember((long)1);
        reviewResultRepository.save(reviewResult);
        reviewResults.add(reviewResult);
        paper.setReviewResults(reviewResults);
        paperRepository.save(paper);
        assertEquals("Please confirm or revise the result after you have discussed with other PC MEMBER", paperService.reviseOrConfirmReviewResult(2, "comment", "confidence", reviewResult.getId(), user.getUsername()));

        conference.setStatus("OPEN_REVIEW");
        conferenceRepository.save(conference);
        assertEquals(NO_AUTHORITY, paperService.reviseOrConfirmReviewResult(2, "comment", "confidence", reviewResult.getId(), user.getUsername()));

        reviewResult.setIdOfPcMember(user.getId());
        reviewResult.setConfirm(2);
        reviewResultRepository.save(reviewResult);
        assertEquals("you have already confirmed or revised the result!!", paperService.reviseOrConfirmReviewResult(2, "comment", "confidence", reviewResult.getId(), user.getUsername()));

        reviewResult.setConfirm(0);
        reviewResultRepository.save(reviewResult);
        assertEquals(SUCCESS, paperService.reviseOrConfirmReviewResult(2, "comment", "confidence", reviewResult.getId(), user.getUsername()));
    }

    @Test
    @Rollback(false)
    void openFinalResult(){
        Conference conference = new Conference("testForOpenFinalResult",
                "testForOpenFinalResult",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userNotForOpenFinalResult",
                "topic");
        conferenceRepository.save(conference);

        User user = new User("userForOpenFinalResult",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForOpenFinalResult",
                "USER");

        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setTopics("topic");
        userConferenceRepository.save(userAndConference);
        assertEquals(NO_AUTHORITY, paperService.openFinalResult(conference.getId(), user.getUsername()));

        conference.setOwner("userForOpenFinalResult");
        conferenceRepository.save(conference);
        assertEquals(SUCCESS, paperService.openFinalResult(conference.getId(), user.getUsername()));

        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paperRepository.save(paper);
        Set<Paper> papers = new HashSet<>();
        userAndConference.setPapers(papers);
        userConferenceRepository.save(userAndConference);

        Set<ReviewResult> reviewResults = new HashSet<>();
        ReviewResult reviewResult = new ReviewResult();
        reviewResult.setPaper(paper);
        reviewResult.setConfirm(1);
        reviewResultRepository.save(reviewResult);
        reviewResults.add(reviewResult);
        paper.setRebuttal("rebuttal");
        paper.setReviewResults(reviewResults);
        paperRepository.save(paper);
        assertEquals("wait for all review results to be confirmed or revised!", paperService.openFinalResult(conference.getId(), user.getUsername()));
    }

    @Test
    @Rollback(false)
    void judgeIsPcMember(){
        Conference conference = new Conference("testForJudgeIsPcMember",
                "testForJudgeIsPcMember",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "test",
                "userForJudgeIsPcMember",
                "topic");
        conferenceRepository.save(conference);

        User user = new User("userForJudgeIsPcMember",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userForJudgeIsPcMember",
                "USER");

        Set<Conference> conferences = new HashSet<>();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        UserAndConference userAndConference = new UserAndConference();
        userAndConference.setUserID(user.getId());
        userAndConference.setConferenceID(conference.getId());
        userAndConference.setTopics("topic");
        userConferenceRepository.save(userAndConference);

        Paper paper = new Paper("title", "summary", conference.getId(), user.getId(), "pdf", "url", new Timestamp(200000));
        paperRepository.save(paper);
        Set<Paper> papers = new HashSet<>();
        userAndConference.setPapers(papers);
        userConferenceRepository.save(userAndConference);

        Set<ReviewResult> reviewResults = new HashSet<>();
        ReviewResult reviewResult = new ReviewResult();
        reviewResult.setPaper(paper);
        reviewResult.setConfirm(1);
        reviewResult.setIdOfPcMember(user.getId());
        reviewResult.setPcMember(true);
        reviewResultRepository.save(reviewResult);
        reviewResults.add(reviewResult);
        paper.setReviewResults(reviewResults);
        paperRepository.save(paper);

        assertEquals(paper, paperService.judgeIsPcMember(paper, user.getUsername()));
    }
}