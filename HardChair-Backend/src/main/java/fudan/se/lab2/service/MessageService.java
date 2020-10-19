package fudan.se.lab2.service;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Message;
import fudan.se.lab2.domain.StatusOfInvitation;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.MessageRepository;
import fudan.se.lab2.repository.StatusOfInvitationRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;

@Service
public class MessageService {

    private ConferenceRepository conferenceRepository;
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private StatusOfInvitationRepository statusOfInvitationRepository;

    private static final String CONFERENCE_NOT_FOUND  = "conferenceNotFound";
    private static final String USER_NOT_FOUND  = "userNotFound";
    private static final String SUCCESS  = "success";

    @Autowired
    public MessageService(ConferenceRepository conferenceRepository, UserRepository userRepository, MessageRepository messageRepository, StatusOfInvitationRepository statusOfInvitationRepository){
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.statusOfInvitationRepository = statusOfInvitationRepository;
    }

    public String sendPCMemberInvitation(String usernameOfChair, Long conferenceID, Long iDOfUserToBeSent){
        Conference conference = conferenceRepository.findConferenceById(conferenceID);
        if(conference==null){ return CONFERENCE_NOT_FOUND; }
        User user = userRepository.findUserById(iDOfUserToBeSent);
        if(user==null){ return USER_NOT_FOUND; }
        Message message = new Message();
        message.setType("PC_MEMBER_INVITATION");
        message.setStatus(0);//未读
        message.setContent(usernameOfChair+","+conference.getFullName()+","+conferenceID);
        message.setUser(user);
        message.setSentTime(new Timestamp(System.currentTimeMillis()));
        message.setTag(conference.getTopics());
        messageRepository.save(message);
        StatusOfInvitation statusOfInvitation = new StatusOfInvitation();
        statusOfInvitation.setConferenceId(conferenceID);
        statusOfInvitation.setUserToSentId(user.getId());
        statusOfInvitation.setStatus("UNREAD");
        statusOfInvitation.setUsername(user.getUsername());
        statusOfInvitationRepository.save(statusOfInvitation);
        return SUCCESS;
    }

    public String sendConferenceCheckedOrAbolishedMessage(Long conferenceID, Long iDOfUserToBeSent, String checkedOrAbolished){
        User user = userRepository.findUserById(iDOfUserToBeSent);
        if(user==null){ return USER_NOT_FOUND; }
        Conference conference = conferenceRepository.findConferenceById(conferenceID);
        if(conference==null){ return CONFERENCE_NOT_FOUND; }
        Message message = new Message();
        message.setType(checkedOrAbolished);
        message.setStatus(0);
        message.setContent("" + conference.getFullName() + ","+conferenceID);

        message.setUser(user);
        message.setSentTime(new Timestamp(System.currentTimeMillis()));
        messageRepository.save(message);
        return SUCCESS;
    }

    public String sendPCMemberAcceptedOrRejectedMessage(Long conferenceID, String username, String acceptOrReject){
        Conference conference = conferenceRepository.findConferenceById(conferenceID);
        if(conference==null){ return CONFERENCE_NOT_FOUND; }
        User owner = userRepository.findByUsername(conference.getOwner());
        if(owner==null){ return "ownerNotFound"; }
        User user = userRepository.findByUsername(username);
        if(user==null){ return USER_NOT_FOUND; }
        Message message = new Message();
        message.setType(acceptOrReject);
        message.setStatus(0);
        message.setContent("" + user.getUsername()+"," + conference.getFullName() + ","+conferenceID);

        message.setUser(owner);
        message.setSentTime(new Timestamp(System.currentTimeMillis()));
        messageRepository.save(message);
        StatusOfInvitation statusOfInvitation = statusOfInvitationRepository.findStatusOfInvitationsByConferenceIdAndUserToSentId(conferenceID, user.getId());
        if(acceptOrReject.equals("PC_MEMBER_ACCEPTED")){
            statusOfInvitation.setStatus("ACCEPT");
        }else if(acceptOrReject.equals("PC_MEMBER_REJECTED")){
            statusOfInvitation.setStatus("REJECT");
        }
        statusOfInvitationRepository.save(statusOfInvitation);
        return SUCCESS;
    }

    public Set<Message> getAllMessagesOfUser(String username){
        return userRepository.findByUsername(username).getMessages();
    }

    public String updateStatus(Long messageId){
        Message message = messageRepository.findMessageByMessageId(messageId);
        if(message == null){
            return "messageNotFound";
        }
        message.setStatus(1);
        messageRepository.save(message);
        return SUCCESS;
    }
}
