package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.VerifyRequest;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.service.ConferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class VerificationController {

    private ConferenceService conferenceService;

    Logger logger = LoggerFactory.getLogger(VerificationController.class);

    @Autowired
    public VerificationController(ConferenceService conferenceService){
        this.conferenceService = conferenceService;
    }

    @GetMapping("/Verification")
    public ResponseEntity<Set<Conference>> verification(){
        return ResponseEntity.ok(conferenceService.getUncheckedConferences());
    }


    /**
     * verify the conference
     * When debug, delete the param "request"
     * @param request request from the front end
     * @return response
     */
    @PostMapping("/Verify")
    public ResponseEntity<String> verify(@RequestBody VerifyRequest request){
        logger.debug("verify request");
        String username = conferenceService.findConference(request.getId()).getOwner();
        Conference conference = conferenceService.findConference(request.getId());
        if(conference==null|| conference.getStatus().equals("CHECKED")){
            return ResponseEntity.ok("HANDLED");
        }
        conferenceService.verifyConference(request.getId(),username,request.getIsAllowed());
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/VerifyUpdate")
    public ResponseEntity<Boolean> updateVerify(){
        boolean hasUncheckedConference = true;
        if(conferenceService.getUncheckedConferences().isEmpty()){
            hasUncheckedConference = false;
        }
        return ResponseEntity.ok(hasUncheckedConference);
    }
}
