package fudan.se.lab2.service;

import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;

@Service
public class PaperService {

    private PostRepository postRepository;
    private PaperRepository paperRepository;
    private ConferenceService conferenceService;
    private UserRepository userRepository;
    private ConferenceRepository conferenceRepository;
    private ReviewResultRepository reviewResultRepository;

    private static final String NO_AUTHORITY = "No Authority";
    private static final String SUCCESS = "success";

    @Autowired
    public PaperService(PostRepository postRepository, PaperRepository paperRepository, ConferenceService conferenceService, UserRepository userRepository, ConferenceRepository conferenceRepository, ReviewResultRepository reviewResultRepository){
        this.postRepository = postRepository;
        this.paperRepository = paperRepository;
        this.conferenceService = conferenceService;
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.reviewResultRepository = reviewResultRepository;
    }


    public String submitPost(String postContent, String username, Long paperId, Long quoteId){
        Paper paper = paperRepository.findPaperById(paperId);
        int status = parseStatus(paper.getConferenceId());
        //只有开启评审和开启结果状态中的会议才可以发帖
        if(status < 0) return "fail: conference status";
        //判断当前用户是否是被分配到该文章的pc member
        //如果即没有被分配，又不是chair，则无权限发帖
        if(conferenceService.checkReview(paperId, username).equals(NO_AUTHORITY)&&(!conferenceService.checkChair(username, paper.getConferenceId())))
            return NO_AUTHORITY;
        Post post = new Post(postContent, new Timestamp(System.currentTimeMillis()), username, status);
        post.setQuoteId(quoteId);
        post.setPaper(paper);
        postRepository.save(post);
        return SUCCESS;
    }

    public String submitRebuttal(String rebuttalContent, String username, Long paperId){
        Paper paper = paperRepository.findPaperById(paperId);
        User user = userRepository.findByUsername(username);

        if(!paper.getUserId().equals(user.getId()))
            return NO_AUTHORITY;

        //会议状态为open result时才可提交rebuttal
        Conference conference = conferenceRepository.findConferenceById(paper.getConferenceId());
        if(!conference.getStatus().equals("OPEN_RESULT"))
            return NO_AUTHORITY;

        //rebuttal不可重复提交
        if(paper.getRebuttal()!=null)
            return "you have already submitted the rebuttal!";

        paper.setRebuttal(rebuttalContent);
        paperRepository.save(paper);
        return SUCCESS;
    }

    //不管是revise还是confirm，前端都发全部内容，后端拿新内容覆盖原内容并修改状态（阶段）即可
    public String reviseOrConfirmReviewResult(int score, String comment, String confidence, Long reviewResultId, String username){
        User user = userRepository.findByUsername(username);
        //首先判断当前阶段讨论区是否有帖子，有帖子才能confirm哦～
        ReviewResult reviewResult = reviewResultRepository.findReviewResultById(reviewResultId);
        Paper paper = reviewResult.getPaper();

        int status = parseStatus(paper.getConferenceId());

        Set<Post> posts = paper.getPosts();
        boolean flag = false;
        for(Post post : posts){
            if(post.getStatus() == status){
                flag = true;
                break;
            }
        }
        if(!flag)
            return "Please confirm or revise the result after you have discussed with other PC MEMBER";

        //判断修改或确认当前评审结果的是否为对应PC MEMBER
        if(!reviewResult.getIdOfPcMember().equals(user.getId()))
            return NO_AUTHORITY;

        //判断在本阶段是否已经确认/修改reviewResult，根据需求，每阶段只能确认/修改一次！
        if(reviewResult.getConfirm() >= status)
            return "you have already confirmed or revised the result!!";

        if(score==1){
            score = -2;
        }else if(score==2){
            score = -1;
        }else if(score==3){
            score = 1;
        }else if(score==4){
            score = 2;
        }
        reviewResult.setScore(score);
        reviewResult.setComment(comment);
        reviewResult.setConfidence(confidence);
        reviewResult.setConfirm(status);

        reviewResultRepository.save(reviewResult);

        return SUCCESS;
    }


    public String openFinalResult(Long conferenceId, String username){
        //只有chair才有变更会议状态的权限
        if(!conferenceService.checkChair(username, conferenceId))
            return NO_AUTHORITY;

        //判断第二轮是否所有的review result都已经确认过了
        if(!conferenceService.checkReviewResultStatus(conferenceId, 2))
            return "wait for all review results to be confirmed or revised!";

        Conference conference = conferenceRepository.findConferenceById(conferenceId);
        conference.setStatus("OPEN_FINAL_RESULT");
        conferenceRepository.save(conference);
        return SUCCESS;

    }

    private int parseStatus(Long conferenceId){
        Conference conference = conferenceRepository.findConferenceById(conferenceId);
        if(conference.getStatus().equals("OPEN_REVIEW")) return 1;
        if(conference.getStatus().equals("OPEN_RESULT")) return 2;
        return -1;
    }

    //只在paper detail页面会有区分
    public Paper judgeIsPcMember(Paper paper, String username){
        User user = userRepository.findByUsername(username);
        Set<ReviewResult> reviewResults = paper.getReviewResults();
        for(ReviewResult reviewResult : reviewResults){
            if(reviewResult.getIdOfPcMember().equals(user.getId()))
                reviewResult.setPcMember(true);
        }
        return paper;
    }

}
