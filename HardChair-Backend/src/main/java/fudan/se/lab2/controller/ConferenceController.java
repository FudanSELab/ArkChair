package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.*;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.ConferenceService;
import fudan.se.lab2.service.JwtUserDetailsService;
import fudan.se.lab2.service.MessageService;
import fudan.se.lab2.service.PaperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
public class ConferenceController {

    private ConferenceService conferenceService;
    private JwtUserDetailsService userDetailsService;
    private MessageService messageService;
    private PaperService paperService;
    private static final String MESSAGE  = "message";

    Logger logger = LoggerFactory.getLogger(ConferenceController.class);

    @Autowired
    public ConferenceController(ConferenceService conferenceService, JwtUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, MessageService messageService, PaperService paperService) {
        this.conferenceService = conferenceService;
        this.userDetailsService = userDetailsService;
        this.messageService = messageService;
        this.paperService = paperService;
    }

    @PostMapping("/ConferenceApplication")
    public ResponseEntity<Conference> createConference(@RequestBody ConferenceRequest request){
        logger.debug("Conference create");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Conference conference = conferenceService.createConference(request, user.getUsername());
        return ResponseEntity.ok(conference);
    }

    @PostMapping("/ConferenceOpenSubmit")
    public ResponseEntity<Map<String, String>> openSubmit(@RequestBody ConferenceOpenSubmitRequest request){
        logger.debug("Open Submit");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long conferenceId = request.getConferenceId();
        Map<String, String> response = new HashMap<>();
        if(conferenceService.checkChair(user.getUsername(), conferenceId)) {
            String message = conferenceService.openSubmit(conferenceId);
            response.put(MESSAGE, message);
        }else{
            response.put(MESSAGE, "authority false");
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/SearchByFullName")
    public ResponseEntity<Set<User>> searchByFullName(@RequestBody SearchByFullNameRequest request){
        logger.debug("Search by fullname");

        Set<User> users = conferenceService.searchByFullName(request.getFullname());

        User chair = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long conferenceId = request.getConferenceId();
        String conferenceFullname = conferenceService.findConference(conferenceId).getFullName();
        String content = chair.getUsername()+","+conferenceFullname+","+request.getConferenceId();

        Set<User> remove = new HashSet<>();
        for(User user : users){
            Set<Message> messages = user.getMessages();
            for(Message message : messages){
                if(content.equals(message.getContent())){
                    remove.add(user);
                    break;
                }
            }
            if(user.getUsername().equals(chair.getUsername())){
                remove.add(user);
            }
        }
        for(User user : remove){
            users.remove(user);
        }

        return ResponseEntity.ok(users);
    }

    @PostMapping("/ConferenceDetails")
    public ResponseEntity<Map<String, Object>> conferenceDetails(@RequestBody ConferenceDetailRequest request){
        logger.debug("conference detail");

        Long conferenceID = request.getId();
        Conference conference = conferenceService.findConference(conferenceID);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Authority> authorities = conferenceService.findAuthorityOfUser(user.getUsername(), conferenceID);
        Set<Paper> papers = conferenceService.findPapersByConferenceIdAndUserId(conferenceID, user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("authorities", authorities);
        map.put("conference", conference);
        map.put("papers", papers);
        return ResponseEntity.ok(map);
    }

    @PostMapping(value = "/DistributeAuthority",produces="application/json")
    public ResponseEntity<Map<String, String>> distributeAuthority(@RequestBody DistributeAuthorityRequest request){
        logger.debug("distribute authority");
        //token
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usernameOfChair = user.getUsername();
        Long conferenceID = request.getConferenceId();
        Long[] users = request.getUsers();
        Map<String, String> response = new HashMap<>();

        Conference conference = conferenceService.findConference(conferenceID);
        String content = conference.getOwner()+","+conference.getFullName()+","+conferenceID;

        Set<Long> remove = new HashSet<>();
        for(long userId : users){
            User userInit = userDetailsService.findUserById(userId);
            Set<Message> messages = userInit.getMessages();
            for(Message message : messages){
                if(content.equals(message.getContent())){
                    remove.add(userInit.getId());
                    break;
                }
            }
        }

        if(conferenceService.checkChair(usernameOfChair, conferenceID)){
            for(long userToSent : users) {
                if(!remove.contains(userToSent)){
                    messageService.sendPCMemberInvitation(usernameOfChair, conferenceID, userToSent);
                }
            }
            response.put(MESSAGE, "success");
        }else{
            response.put(MESSAGE, "fail");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Show all checked / submit_allowed conferences
     * Require login status !!!
     * @return response
     */
    @GetMapping("/ShowConferences")
    public ResponseEntity<Map<String, Set<Conference>>> showConferences(){
        Map<String, Set<Conference>> map = new HashMap<>();
        map.put("CHECKED", conferenceService.getCheckedConferences());
        map.put("SUBMIT_ALLOWED", conferenceService.getSubmitAllowedConferences());
        map.put("OPEN_REVIEW", conferenceService.getOpenReviewConferences());
        map.put("OPEN_RESULT", conferenceService.getOpenResultConferences());
        map.put("OPEN_FINAL_RESULT", conferenceService.getOpenFinalResultConferences());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/SubmitPaper")
    public ResponseEntity<Object> submitPaper(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long conferenceId = Long.parseLong(request.getParameter("conferenceId"));
        Conference conference = conferenceService.findConference(conferenceId);
        if(user.getUsername().equals(conference.getOwner())){
            return new ResponseEntity<>("Not allowed",HttpStatus.FORBIDDEN);
        }

        try {
            String messageOfUpload = conferenceService.uploadPaper(request, user.getId(), "upload");
            response.put("messageOfUpload", messageOfUpload);
            if (!messageOfUpload.equals("upload successful")){
                response.put("messageOfChangeAuthority", "fail");
                return ResponseEntity.ok(response);
            }

            String username = user.getUsername();
            String messageOfChangeAuthority = conferenceService.updateAuthorityOfUserInConference(username, "AUTHOR", conferenceId);
            response.put("messageOfChangeAuthority", messageOfChangeAuthority);
        }catch (IOException e){
            logger.debug(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/RevisePaper")
    public ResponseEntity<Map<String, String>> revisePaper(HttpServletRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, String> response = new HashMap<>();

        try {
            String messageOfRevise = conferenceService.uploadPaper(request, user.getId(), "revise");
            response.put("messageOfRevise", messageOfRevise);
        }catch (IOException e){
            logger.debug(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/FindInvitationStatus")
    public ResponseEntity<Object> findInvitationStatus(@RequestBody FindInvitationStatusRequest request){
        return ResponseEntity.ok(conferenceService.findInvitationStatus(request.getConferenceId()));
    }

    @PostMapping("/OpenReview")
    public ResponseEntity<Map<String, String>> openReview(@RequestBody OpenReviewRequest request){
        Map<String, String> response = new HashMap<>();
        String message1 = conferenceService.openReview(request.getConferenceId());
        if(message1.equals("open success")) {
            String message2 = null;
            if (request.getStrategy() == 1) {
                message2 = conferenceService.distributeMethodOne(request.getConferenceId());
            } else if (request.getStrategy() == 2) {
                message2 = conferenceService.distributeMethodTwo(request.getConferenceId());
            }
            assert message2 != null;
            if(!message2.equals("success")){
                message1 = message2;
            }
            response.put("message2", message2);
        }
        response.put("message1", message1);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ReviewPapers")
    public ResponseEntity<Object> reviewPapers(@RequestBody ReviewPaperRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(conferenceService.reviewPapers(user.getUsername(), request.getConferenceId()));
    }

    @PostMapping("/SubmitReviewResult")
    public ResponseEntity<Map<String, String>> submitReviewResult(@RequestBody SubmitReviewResultRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = conferenceService.submitReviewResult(user.getUsername(), request.getPaperId(), request.getScore(), request.getComment(), request.getConfidence());
        Map<String, String> response = new HashMap<>();
        response.put(MESSAGE, message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/OpenResult")
    public ResponseEntity<Map<String, String>> openResult(@RequestBody OpenReviewRequest request){
        String message = conferenceService.openResult(request.getConferenceId());
        Map<String, String> response = new HashMap<>();
        response.put(MESSAGE, message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ReviewResults")
    public ResponseEntity<Object> reviewResults(@RequestBody ReviewResultsRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(conferenceService.reviewResults(user.getUsername(), request.getConferenceId()));
    }


    @PostMapping("/DownloadPaper")
    public ResponseEntity<Map<String, String>> downloadPaper(@RequestBody DownloadPaperRequest request, HttpServletResponse response) throws IOException {
        String message = conferenceService.download(request.getPaperId(), response);
        Map<String, String> res = new HashMap<>();
        res.put(MESSAGE, message);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/PaperAuthority")
    public ResponseEntity<Object> paperAuthority(@RequestBody PaperAuthorityRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Paper paper = conferenceService.paperAuthority(user.getUsername(), request.getPaperId());
        paper = paperService.judgeIsPcMember(paper, user.getUsername());
        if(paper.getUrl().equals("AUTHOR")||paper.getUrl().equals("PC_MEMBER")||paper.getUrl().equals("CHAIR")||paper.getUrl().equals("CP")){
            return ResponseEntity.ok(paper);
        }else{
            Map<String, String> res = new HashMap<>();
            res.put(MESSAGE, "NONE");
            return ResponseEntity.ok(res);
        }
    }

}
