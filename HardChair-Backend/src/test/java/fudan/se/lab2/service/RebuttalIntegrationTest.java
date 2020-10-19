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
class RebuttalIntegrationTest {

    private UserRepository userRepository;
    private ConferenceRepository conferenceRepository;
    private AuthorityRepository authorityRepository;
    private User_ConferenceRepository userConferenceRepository;
    private PaperRepository paperRepository;
    private PaperService paperService;
    private ReviewResultRepository reviewResultRepository;

    @Autowired
    public RebuttalIntegrationTest(UserRepository userRepository, ConferenceRepository conferenceRepository, AuthorityRepository authorityRepository, User_ConferenceRepository userConferenceRepository, PaperRepository paperRepository, PaperService paperService, ReviewResultRepository reviewResultRepository){
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.authorityRepository = authorityRepository;
        this.userConferenceRepository = userConferenceRepository;
        this.paperRepository = paperRepository;
        this.paperService = paperService;
        this.reviewResultRepository = reviewResultRepository;
    }

    @Test
    void submitRebuttalAndDiscussAndConfirm(){
        User userChair = new User("userChairForIntegratedTestSRADAC",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userChairForIntegratedTestSRADAC",
                "USER");
        userRepository.save(userChair);

        Conference conference = new Conference("conferenceForIntegratedTestSRADAC",
                "conferenceForIntegratedTestSRADAC",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "SH",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "OPEN_RESULT",
                "userChairForIntegratedTestSRADAC",
                "topic1");
        conferenceRepository.save(conference);

        UserAndConference userAndConference = new UserAndConference(userChair.getId(), conference.getId());
        Authority authority1 = authorityRepository.findByAuthority("CHAIR");
        Authority authority2 = authorityRepository.findByAuthority("PC_MEMBER");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority1);
        authorities.add(authority2);
        userAndConference.setAuthorities(authorities);
        userConferenceRepository.save(userAndConference);

        User userAuthor = new User("userAuthorForIntegratedTestSRADAC",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userAuthorForIntegratedTestSRADAC",
                "USER");
        userRepository.save(userAuthor);

        Paper paper = new Paper("paperForIntegratedTestSRADAC", "summary", conference.getId(), userAuthor.getId(), "pdf", "url", new Timestamp(200000));
        paperRepository.save(paper);
        UserAndConference userAndConference1 = new UserAndConference(userAuthor.getId(), conference.getId());
        Authority authority3 = authorityRepository.findByAuthority("AUTHOR");
        Set<Authority> authorities1 = new HashSet<>();
        authorities1.add(authority3);
        userAndConference1.setAuthorities(authorities1);
        userConferenceRepository.save(userAndConference1);

        User userPC1 = new User("userPC1ForIntegratedTestSRADAC",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userPC1ForIntegratedTestSRADAC",
                "USER");
        userRepository.save(userPC1);

        User userPC2 = new User("userPC2ForIntegratedTestSRADAC",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "userPC2ForIntegratedTestSRADAC",
                "USER");
        userRepository.save(userPC2);


        Set<Authority> authorities2 = new HashSet<>();
        authorities2.add(authority2);
        UserAndConference userAndConference2 = new UserAndConference(userPC1.getId(), conference.getId());
        userAndConference2.setAuthorities(authorities2);
        userConferenceRepository.save(userAndConference2);
        UserAndConference userAndConference3 = new UserAndConference(userPC2.getId(), conference.getId());
        userAndConference3.setAuthorities(authorities2);
        userConferenceRepository.save(userAndConference3);

        //distribute paper to the PC members
        Set<Paper> papers = new HashSet<>();
        papers.add(paper);
        userAndConference.setPapers(papers);
        userAndConference2.setPapers(papers);
        userAndConference3.setPapers(papers);
        userConferenceRepository.save(userAndConference);
        userConferenceRepository.save(userAndConference2);
        userConferenceRepository.save(userAndConference3);

        //set review result
        paper = paperRepository.findPaperById(paper.getId());
        ReviewResult reviewResult1 = new ReviewResult(-2, "comment", "confidence", userChair.getId());
        ReviewResult reviewResult2 = new ReviewResult(-2, "comment", "confidence", userPC1.getId());
        ReviewResult reviewResult3 = new ReviewResult(-2, "comment", "confidence", userPC2.getId());
        reviewResult1.setPaper(paper);
        reviewResult2.setPaper(paper);
        reviewResult3.setPaper(paper);
        reviewResultRepository.save(reviewResult1);
        reviewResultRepository.save(reviewResult2);
        reviewResultRepository.save(reviewResult3);
        Set<ReviewResult> reviewResults = new HashSet<>();
        reviewResults.add(reviewResult1);
        reviewResults.add(reviewResult2);
        reviewResults.add(reviewResult3);
        paper.setStatus(3);
        paper.setReviewResults(reviewResults);
        paperRepository.save(paper);

        //user submit rebuttal
        assertEquals("success", paperService.submitRebuttal("my rebuttal", userAuthor.getUsername(), paper.getId()));

        //pc members discuss
        assertEquals("success", paperService.submitPost("hi im chair", userChair.getUsername(), paper.getId(), (long)-1));
        assertEquals("success", paperService.submitPost("hi im pc1", userPC1.getUsername(), paper.getId(), (long)-1));
        assertEquals("success", paperService.submitPost("hi im pc2", userPC2.getUsername(), paper.getId(), (long)-1));

        //you can't open final result if there exists paper with rebuttal unsolved
        assertEquals("wait for all review results to be confirmed or revised!", paperService.openFinalResult(conference.getId(), userChair.getUsername()));

        //pc members submit revised result
        assertEquals("success", paperService.reviseOrConfirmReviewResult(2, "comment", "confidence", reviewResult1.getId(), userChair.getUsername()));
        assertEquals("success", paperService.reviseOrConfirmReviewResult(2, "comment", "confidence", reviewResult2.getId(), userPC1.getUsername()));
        assertEquals("success", paperService.reviseOrConfirmReviewResult(2, "comment", "confidence", reviewResult3.getId(), userPC2.getUsername()));

        //open final result
        assertEquals("success", paperService.openFinalResult(conference.getId(), userChair.getUsername()));
        assertEquals("OPEN_FINAL_RESULT", conferenceRepository.findConferenceById(conference.getId()).getStatus());
    }

}