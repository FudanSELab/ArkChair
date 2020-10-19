package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.AuthorityAcceptedOrRejectedRequest;
import fudan.se.lab2.controller.request.MessageAlreadyReadRequest;
import fudan.se.lab2.domain.Message;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.service.ConferenceService;
import fudan.se.lab2.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class MessageController {
    private ConferenceService conferenceService;
    private MessageService messageService;

    @Autowired
    public MessageController(ConferenceService conferenceService, MessageService messageService){
        this.conferenceService = conferenceService;
        this.messageService = messageService;
    }

    @PostMapping("/AuthorityAcceptedOrRejected")
    public ResponseEntity<Map<String, String>> authorityAcceptedOrRejected(@RequestBody AuthorityAcceptedOrRejectedRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        Long conferenceId = request.getConferenceId();
        Long messageId = request.getMessageId();

        Map<String, String> response = new HashMap<>();

        if(request.getAcceptOrRejected().equals("accept")) {
            String messageOfUpdateAuthorityOfUserInConference = conferenceService.updateAuthorityOfUserInConference(username, "PC_MEMBER", conferenceId);
            String message = messageService.sendPCMemberAcceptedOrRejectedMessage(conferenceId, username, "PC_MEMBER_ACCEPTED");
            response.put("message", message);
            String message2 = conferenceService.addTopics(conferenceId, username, request.getTopics());
            response.put("message2", message2);
            response.put("messageOfUpdateAuthorityOfUserInConference", messageOfUpdateAuthorityOfUserInConference);
        }else if(request.getAcceptOrRejected().equals("reject")){
            String message = messageService.sendPCMemberAcceptedOrRejectedMessage(conferenceId, username, "PC_MEMBER_REJECTED");
            response.put("message", message);
        }
        messageService.updateStatus(messageId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Message")
    public ResponseEntity<Set<Message>> searchAllMessagesOfUser(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Message> set= messageService.getAllMessagesOfUser(user.getUsername());
        return ResponseEntity.ok(set);
    }

    @GetMapping("/MessageUpdate")
    public ResponseEntity<Boolean> updateMessage(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean hasNewMessage = false;
        Set<Message> set= messageService.getAllMessagesOfUser(user.getUsername());
        for(Message message :set){
            if(message.getStatus()==0){
                hasNewMessage = true;
                break;
            }
        }
        return ResponseEntity.ok(hasNewMessage);
    }

    @PostMapping("/MessageAlreadyRead")
    public ResponseEntity<Object> messageAlreadyRead(@RequestBody MessageAlreadyReadRequest request){
        Long messageId = request.getMessageId();
        return ResponseEntity.ok(messageService.updateStatus(messageId));
    }

}
