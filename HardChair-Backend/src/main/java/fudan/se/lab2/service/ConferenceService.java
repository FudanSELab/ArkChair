package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import fudan.se.lab2.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ConferenceService {

    private ConferenceRepository conferenceRepository;
    private User_ConferenceRepository userConferenceRepository;
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private MessageService messageService;
    private PaperRepository paperRepository;
    private StatusOfInvitationRepository statusOfInvitationRepository;
    private TopicRepository topicRepository;
    private AuthorRepository authorRepository;
    private ReviewResultRepository reviewResultRepository;
    private Random rand = SecureRandom.getInstance("NativePRNGNonBlocking");

    Logger logger = LoggerFactory.getLogger(ConferenceService.class);

    private static final String SUCCESS = "success";
    private static final String FAILED = "open fail : no solution";
    private static final String PC_MEMBER = "PC_MEMBER";
    private static final String AUTHOR = "AUTHOR";
    private static final String CHAIR = "CHAIR";
    private static final String NO_AUTHORITY = "No Authority";
    private static final String ALREADY_REVIEW = "Already Review";
    private static final String SUBMIT_ALLOWED = "SUBMIT_ALLOWED";

    @Autowired
    public ConferenceService(ConferenceRepository conferenceRepository, User_ConferenceRepository userConferenceRepository, UserRepository userRepository, AuthorityRepository authorityRepository, MessageService messageService, PaperRepository paperRepository, StatusOfInvitationRepository statusOfInvitationRepository, TopicRepository topicRepository, AuthorRepository authorRepository, ReviewResultRepository reviewResultRepository) throws NoSuchAlgorithmException {
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
    }

    public Conference createConference(ConferenceRequest request, String username) {
        Conference conference = null;
        UserAndConference userAndConference = null;
        conference = new Conference(
                request.getNameAbbreviation(),
                request.getFullName(),
                DateUtil.transferDateFormat(request.getTime()[0]),
                DateUtil.transferDateFormat(request.getTime()[1]),
                request.getLocation(),
                DateUtil.transferDateFormat(request.getDeadline()),
                DateUtil.transferDateFormat(request.getResultAnnounceDate()),
                "UNCHECKED",
                username,
                parseTopics(request.getTopic())
        );


        String[] requestTopics = request.getTopic();
        Set<Topic> topics = new HashSet<>();
        for (String requestTopic : requestTopics) {
            Topic topic = topicRepository.findTopicByTopic(requestTopic);
            if (topic == null) {
                topic = new Topic(requestTopic);
                topicRepository.save(topic);
            }
            topics.add(topic);
        }


        conferenceRepository.save(conference);

        User user = userRepository.findByUsername(username);
        Set<Conference> conferences = user.getConferences();
        conferences.add(conference);
        user.setConferences(conferences);
        userRepository.save(user);

        Authority authority = authorityRepository.findByAuthority(CHAIR);
        userAndConference = new UserAndConference(user.getId(), conference.getId());
        Set<Authority> authorities = userAndConference.getAuthorities();
        authorities.add(authority);
        userAndConference.setAuthorities(authorities);
        //默认负责所有topic
        userAndConference.setTopics(parseTopics(request.getTopic()));
        userConferenceRepository.save(userAndConference);

        return conference;
    }

    public Set<User> searchByFullName(String fullname){
        Set<User> users = userRepository.findAllByFullname(fullname);
        users.removeIf(user -> user.getAuthority().equals("ADMIN"));
        return users;
    }

    public String openSubmit(Long conferenceId){
        Conference conference = conferenceRepository.findConferenceById(conferenceId);
        if(conference == null){
            return "conferenceNotFound";
        }
        conference.setStatus(SUBMIT_ALLOWED);
        conferenceRepository.save(conference);
        return SUCCESS;
    }

    public Conference findConference(Long conferenceId){
        return conferenceRepository.findConferenceById(conferenceId);
    }

    public Set<Authority> findAuthorityOfUser(String username, Long conferenceId){
        User user = userRepository.findByUsername(username);
        UserAndConference userAndConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), conferenceId);
        if(userAndConference ==null){
            return new HashSet<>();
        }
        return userAndConference.getAuthorities();
    }

    public boolean checkChair(String username, Long conferenceId){
        return conferenceRepository.findConferenceById(conferenceId).getOwner().equals(username);
    }

    public String updateAuthorityOfUserInConference(String username, String nameOfAuthority, Long conferenceId){
        User user = userRepository.findByUsername(username);
        if(user == null){
            return "userNotFound";
        }
        Conference conference = conferenceRepository.findConferenceById(conferenceId);
        if(conference == null){
            return "conferenceNotFound";
        }
        Set<Conference> conferences = user.getConferences();
        if(!conferences.contains(conference)) {
            conferences.add(conference);
            user.setConferences(conferences);
        }
        userRepository.save(user);

        Authority authority = authorityRepository.findByAuthority(nameOfAuthority);
        UserAndConference userAndConference;
        if((userAndConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), conferenceId))==null) {
            userAndConference = new UserAndConference(user.getId(), conference.getId());
        }
        Set<Authority> authorities = userAndConference.getAuthorities();
        if(!authorities.contains(authority)) {
            authorities.add(authority);
            userAndConference.setAuthorities(authorities);
        }
        userConferenceRepository.save(userAndConference);
        return SUCCESS;
    }

    /**
     * set conference status
     * @param conferenceId id
     * @param isAllowed is allowed
     */
    @Transactional
    public void verifyConference(Long conferenceId, String username, boolean isAllowed){
        Conference conference = findConference(conferenceId);
        User user = userRepository.findByUsername(username);
        if(isAllowed){
            messageService.sendConferenceCheckedOrAbolishedMessage(conferenceId, user.getId(), "CONFERENCE_CHECKED");
            conference.setStatus("CHECKED");
            conferenceRepository.save(conference);
            UserAndConference userAndConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(),conferenceId);
            Set<Authority> authorities = userAndConference.getAuthorities();
            authorities.add(authorityRepository.findByAuthority(PC_MEMBER));
            userAndConference.setAuthorities(authorities);
            userConferenceRepository.save(userAndConference);
        } else {
            messageService.sendConferenceCheckedOrAbolishedMessage(conferenceId, user.getId(), "CONFERENCE_ABOLISHED");
            conference.setStatus("ABOLISHED");
            conferenceRepository.deleteConferenceById(conferenceId);
        }
    }

    public Set<Conference> getUncheckedConferences() {
        return conferenceRepository.findConferencesByStatus("UNCHECKED");
    }

    public Set<Conference> getCheckedConferences(){
        return conferenceRepository.findConferencesByStatus("CHECKED");
    }

    public Set<Conference> getSubmitAllowedConferences(){
        return conferenceRepository.findConferencesByStatus(SUBMIT_ALLOWED);
    }

    public Set<Conference> getOpenReviewConferences(){
        return conferenceRepository.findConferencesByStatus("OPEN_REVIEW");
    }

    public Set<Conference> getOpenResultConferences(){
        return conferenceRepository.findConferencesByStatus("OPEN_RESULT");
    }

    public Set<Conference> getOpenFinalResultConferences(){
        return conferenceRepository.findConferencesByStatus("OPEN_FINAL_RESULT");
    }

    public String deleteAuthors(Set<Author> authors){
        for(Author author : authors){
            author.setPaper(null);
            authorRepository.deleteById(author.getId());
        }
        return SUCCESS;
    }

    public String uploadPaper(HttpServletRequest request, Long userId,String uploadOrRevise) throws IOException {
        MultipartHttpServletRequest params=((MultipartHttpServletRequest) request);

        if(checkChair(userRepository.findUserById(userId).getUsername(), Long.parseLong(params.getParameter("conferenceId")))){
            return "Chair can't submit or revise paper!";
        }

        Paper paper = new Paper();
        Long conferenceId = Long.parseLong("0");
        if(uploadOrRevise.equals("revise")){
            paper = paperRepository.findPaperById(Long.parseLong(params.getParameter("paperId")));
            conferenceId = paper.getConferenceId();

            Set<Author> authors = paper.getAuthors();
            deleteAuthors(authors);

        }else if(uploadOrRevise.equals("upload")){
            conferenceId = Long.parseLong(params.getParameter("conferenceId"));
        }
        //接收文件相关信息
        String title = params.getParameter("title");
        String summary = params.getParameter("summary");
        long time = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(time);

        if(uploadOrRevise.equals("upload")||(uploadOrRevise.equals("revise")&&params.getParameter("reviseFile").equals("reviseFile"))) {
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");

            String url = "/usr/local/Lab/papers/" + title + Long.toString(time) + ".pdf";

            assert file != null;
            String type = file.getContentType();
            assert type != null;
            if(!type.equals("application/pdf")){
                return "Only PDF is allowed";
            }
            if (!file.isEmpty()) {
                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(
                        new File(url)))) {
                    byte[] bytes = file.getBytes();

                    stream.write(bytes);
                } catch (Exception e) {
                    return "You failed to upload " + " => " + e.getMessage();
                }
            } else {
                return "You failed to upload " + " because the file was empty.";
            }
            paper.setUrl(url);
            paper.setType(type);
        }
        paper.setTitle(title);
        paper.setSummary(summary);
        paper.setConferenceId(conferenceId);
        paper.setUserId(userId);
        paper.setCreatedTime(timestamp);
        paperRepository.save(paper);


        String[] topics = params.getParameterValues("topic");
        String[] authors = params.getParameter("authors").split(",");
        Set<Author> authorsSet = new HashSet<>();
        int order = 1;
        for(int i = 0; i<authors.length;i+=4){
            Author newAuthor = new Author(authors[i], authors[i+1], authors[i+2], authors[i+3]);
            newAuthor.setOrderOfAuthor(order++);
            authorsSet.add(newAuthor);

            newAuthor.setPaper(paper);
            authorRepository.save(newAuthor);
        }

        paper.setAuthors(authorsSet);
        paper.setTopics(parseTopics(topics));
        paper.setStatus(0);

        paperRepository.save(paper);
        return "upload successful";
    }

    public Set<Paper> findPapersByConferenceIdAndUserId(Long conferenceId, Long userId){
        Set<Paper> papers =  paperRepository.findPapersByConferenceIdAndUserId(conferenceId, userId);

        //chair
        if(conferenceRepository.findConferenceById(conferenceId).getOwner().equals(userRepository.findUserById(userId).getUsername())){
            papers = paperRepository.findPapersByConferenceId(conferenceId);
        }

        if(papers.isEmpty()){
            return new HashSet<>();
        }
        return papers;
    }

    public Set<User> findInvitationStatus(Long conferenceId){
        Set<User> users = new HashSet<>();
        Set<StatusOfInvitation> statusOfInvitations = statusOfInvitationRepository.findStatusOfInvitationsByConferenceId(conferenceId);
        for(StatusOfInvitation statusOfInvitation : statusOfInvitations){
            User user = userRepository.findByUsername(statusOfInvitation.getUsername());
            user.setPassword(statusOfInvitation.getStatus());
            users.add(user);
        }
        return users;
    }

    public String addTopics(Long conferenceId, String username, String[] topics){
        UserAndConference userConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(userRepository.findByUsername(username).getId(), conferenceId);
        if(userConference == null){
            return "user not found in conference";
        }
        userConference.setTopics(parseTopics(topics));

        //add at 5.1 start ======================================
        Set<Topic> topicSet = new HashSet<>();
        for (String s : topics) {
            Topic topic = topicRepository.findTopicByTopic(s);
            if (topic == null) {
                return "topic " + s + " not found";
            } else {
                topicSet.add(topic);
            }
        }
        userConferenceRepository.save(userConference);
        return SUCCESS;
    }

    public String parseTopics(String[] topics){
        StringBuilder topic = new StringBuilder();
        for(int i=0;i<topics.length-1;i++){
            topic.append(topics[i]).append(",");
        }
        topic.append(topics[topics.length - 1]);
        return topic.toString();
    }

    public String openReview(Long conferenceId){
        Conference conference = conferenceRepository.findConferenceById(conferenceId);
        int count = findPCMEMBER(conferenceId).size();
        Set<Paper> papers = paperRepository.findPapersByConferenceId(conferenceId);
        if(count < 3){
            return "open fail: number of PC MEMBER should more than 2";
        }
        if(papers.isEmpty()){
            return "open fail: at least 1 paper is expected";
        }
        else {
            conference.setStatus("OPEN_REVIEW");
            conferenceRepository.save(conference);
            return "open success";
        }
    }

    public String distributeMethodOne(Long conferenceId){
        Set<Paper> papers = paperRepository.findPapersByConferenceId(conferenceId);
        List<User> pcMembers = findPCMEMBER(conferenceId);

        Map<String, Set<Paper>> papersOfTheTopic = new HashMap<>();

        for(Paper paper : papers){
            String topic = paper.getTopics();
            if(!papersOfTheTopic.containsKey(topic)){
                Set<Paper> paperSet = new HashSet<>();
                paperSet.add(paper);
                papersOfTheTopic.put(topic, paperSet);
            }else {
                Set<Paper> paperSet = papersOfTheTopic.get(topic);
                paperSet.add(paper);
            }
        }

        for(Map.Entry<String, Set<Paper>> entry : papersOfTheTopic.entrySet()){
            String key = entry.getKey();
            Set<Paper> paperSet = papersOfTheTopic.get(key);
            List<User> pcMembersRelated = getRelatedPcMembers(key, conferenceId, pcMembers);
            int count = pcMembersRelated.size();
            boolean result;
            boolean hasWay = hasWayToDistributePapers(papers, pcMembersRelated);
            if(count < 3  || !hasWay){
                result = distributeBetweenPcMembers(paperSet, pcMembers, conferenceId);
            }else {
                result = distributeBetweenPcMembers(paperSet, pcMembersRelated, conferenceId);
            }
            if(!result){
                Conference conference = conferenceRepository.findConferenceById(conferenceId);
                conference.setStatus(SUBMIT_ALLOWED);
                conferenceRepository.save(conference);
                return FAILED;
            }
        }
        return SUCCESS;
    }

    public String distributeMethodTwo(Long conferenceId){
        Set<Paper> papers = paperRepository.findPapersByConferenceId(conferenceId);
        List<User> users = findPCMEMBER(conferenceId);
        boolean result = distributeBetweenPcMembers(papers, users, conferenceId);
        if(!result) {
            Conference conference = conferenceRepository.findConferenceById(conferenceId);
            conference.setStatus(SUBMIT_ALLOWED);
            conferenceRepository.save(conference);
            return FAILED;
        }
        return SUCCESS;
    }

    private List<User> getRelatedPcMembers(String topicsSubmit, Long conferenceId, List<User> pcMembers) {
        LinkedList<User> relatedPCMEMBERs = new LinkedList<>();
        String[] topics = topicsSubmit.split(",");
        for (User pcMember : pcMembers) {
            UserAndConference conference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(pcMember.getId(), conferenceId);
            String topicStore = conference.getTopics();
            for(String topic : topics){
                if (topicStore.contains(topic)) {
                    relatedPCMEMBERs.add(pcMember);
                    break;
                }
            }
        }
        return relatedPCMEMBERs;
    }

    public List<User> findPCMEMBER(Long conferenceId){
        Conference conference = conferenceRepository.findConferenceById(conferenceId);
        Set<User> users = conference.getUsers();
        LinkedList<User> userLinkedList = new LinkedList<>();
        for(User user : users){
            Set<Authority> authorities = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), conferenceId).getAuthorities();
            for(Authority authority : authorities){
                if(authority.getAuthority().equals(PC_MEMBER)){
                    userLinkedList.add(user);
                }
            }
        }
        return userLinkedList;
    }

    private boolean distributeBetweenPcMembers(Set<Paper> papers, List<User> pcMembers, Long conferenceId) {
        boolean hasWay = hasWayToDistributePapers(papers, pcMembers);
        if(hasWay){
            distribute(papers, pcMembers, conferenceId);
            return true;
        }else {
            return false;
        }
    }

    private boolean hasWayToDistributePapers(Set<Paper> papers, List<User> pcMembers){
        int numOfPapers = papers.size();
        int numOfPcMembers = pcMembers.size();

        for(User pcMember : pcMembers){
            int numOfRelatedPapers = findRelatedPapers(papers, pcMember);
            if(numOfRelatedPapers > numOfPapers - 3 * numOfPapers / numOfPcMembers){
                return false;
            }
        }
        for(Paper paper : papers){
            int numOfRelatedPcMembers = findRelatedPcMembers(pcMembers, paper);
            if(numOfPcMembers - numOfRelatedPcMembers < 3){
                return false;
            }
        }
        return true;
    }

    private int findRelatedPapers(Set<Paper> papers, User pcMember) {
        int result = 0;
        for(Paper paper : papers){
            if(userRepository.findUserById(paper.getUserId()).getId().equals(pcMember.getId())){
                result++;
            }else if(isAuthor(paper, pcMember)){
                result++;
            }
        }
        return result;
    }

    private int findRelatedPcMembers(List<User> pcMembers, Paper paper) {
        int result = 0;
        for(User pcMember : pcMembers){
            if(paper.getUserId().equals(pcMember.getId())){
                result++;
            }else if(isAuthor(paper, pcMember)){
                result++;
            }
        }
        return result;
    }

    private boolean isAuthor(Paper paper, User pcMember) {
        Set<Author> authors = paper.getAuthors();
        for(Author author : authors){
            if(author.getName().equals(pcMember.getFullname()) && author.getEmail().equals(pcMember.getEmail())){
                return true;
            }
        }
        return false;
    }

    private void distribute(Set<Paper> papers, List<User> pcMembers, Long conferenceId) {
        int numberOfPcMember = pcMembers.size();
        int temp = 0;
        LinkedList<User> members = new LinkedList<>(pcMembers);

        boolean flag = false;
        Set<User> tmpUsers = new HashSet<>();
        Set<User> relatedUsers;

        for(Paper paper : papers){
            if(flag){
                members.addAll(tmpUsers);
                flag = false;
            }
            relatedUsers = getPaperRelatedUsers(paper, pcMembers);
            members.removeAll(relatedUsers);

            for(int i=0; i<3; i++){
                if(members.isEmpty()){
                    temp = 0;
                    tmpUsers = new HashSet<>();
                    members.addAll(pcMembers);
                    Set<UserAndConference> userConferences = paper.getUserAndConference();
                    for(UserAndConference userAndConference : userConferences){
                        User user = userRepository.findUserById(userAndConference.getUserID());
                        members.remove(user);
                        tmpUsers.add(user);
                    }
                    members.removeAll(relatedUsers);
                    flag = true;
                }

                int random = rand.nextInt(numberOfPcMember - temp - relatedUsers.size());
                temp++;
                User user = members.get(random);
                UserAndConference userConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), conferenceId);

                Set<Paper> papers1 = userConference.getPapers();
                papers1.add(paper);
                userConference.setPapers(papers1);
                userConferenceRepository.save(userConference);

                members.remove(user);
            }
            members.addAll(relatedUsers);
        }
    }

    private Set<User> getPaperRelatedUsers(Paper paper, List<User> pcMembers) {
        Set<User> relatedUsers = new HashSet<>();
        for(User pcMember : pcMembers){
            if(userRepository.findUserById(paper.getUserId()).getUsername().equals(pcMember.getUsername())){
                relatedUsers.add(pcMember);
                break;
            }
        }
        Set<Author> authors = paper.getAuthors();
        for(Author author : authors){
            for(User pcMember : pcMembers){
                if(author.getName().equals(pcMember.getUsername()) && author.getEmail().equals(pcMember.getEmail())){
                    relatedUsers.add(pcMember);
                    break;
                }
            }
        }
        return relatedUsers;
    }

    public Set<Paper> reviewPapers(String username, Long conferenceId){
        User user = userRepository.findByUsername(username);
        UserAndConference userConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), conferenceId);
        Set<Paper> papers = userConference.getPapers();
        for(Paper paper : papers){
            Set<ReviewResult> reviewResults = paper.getReviewResults();
            if(reviewResults!=null){
                for(ReviewResult reviewResult : reviewResults){
                    if(reviewResult.getIdOfPcMember().equals(user.getId())){
                        paper.setStatus(-1);
                    }
                }
            }
        }
        return papers;
    }

    public String checkReview(Long paperId, String username){
        User user = userRepository.findByUsername(username);
        Paper paper = paperRepository.findPaperById(paperId);
        Conference conference = conferenceRepository.findConferenceById(paper.getConferenceId());
        UserAndConference userConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), conference.getId());

        //判断是否有PC MEMBER权限
        Set<Authority> authorities = userConference.getAuthorities();
        boolean flag = false;
        for (Authority authority : authorities){
            if(authority.getAuthority().equals(PC_MEMBER)){
                flag = true;
                break;
            }
        }
        if(!flag){
            return NO_AUTHORITY;
        }

        //若有PC MEMBER权限，则再进一步判断是否被分配到了该paper
        Set<Paper> papers = userConference.getPapers();
        flag = false;
        for(Paper paperDistributed : papers){
            if(paperDistributed.getId().equals(paperId)){
                flag = true;
                break;
            }
        }
        if(!flag){
            return NO_AUTHORITY;
        }

        //若被分配到了该paper，则再进一步判断是否已经提交结果
        Set<ReviewResult> reviewResults = paper.getReviewResults();

        for(ReviewResult reviewResult : reviewResults){
            if(reviewResult.getIdOfPcMember().equals(user.getId())){
                return ALREADY_REVIEW;
            }
        }

        return SUCCESS;

    }

    public String submitReviewResult(String username, Long paperId, int score, String comment, String confidence){
        Paper paper = paperRepository.findPaperById(paperId);

        String check = checkReview(paperId, username);
        if(check.equals(NO_AUTHORITY)){
            return NO_AUTHORITY;
        }else if(check.equals(ALREADY_REVIEW)){
            return ALREADY_REVIEW;
        }

        if(score==1){
            score = -2;
        }else if(score==2){
            score = -1;
        }else if(score==3){
            score = 1;
        }else if(score==4){
            score = 2;
        }
        ReviewResult reviewResult = new ReviewResult(score, comment, confidence, userRepository.findByUsername(username).getId());
        Set<ReviewResult> reviewResults = new HashSet<>();
        if(paper.getReviewResults()!=null){
            reviewResults = paper.getReviewResults();
        }

        reviewResult.setPaper(paper);
        reviewResultRepository.save(reviewResult);

        reviewResults.add(reviewResult);
        paper.setReviewResults(reviewResults);
        int status = paper.getStatus() + 1;
        paper.setStatus(status);
        paperRepository.save(paper);
        return SUCCESS;
    }

    public String openResult(Long conferenceId){
        Set<Paper> papers = paperRepository.findPapersByConferenceId(conferenceId);
        boolean judge = true;
        for(Paper paper : papers){
            if(paper.getStatus() != 3){
                judge = false;
                break;
            }
        }
        if(!judge){
            return "open fail: wait for review";
        }

        //revised at 5.31
        if(!checkReviewResultStatus(conferenceId, 1))
            return "wait for all review results to be confirmed or revised!";


        Conference conference = conferenceRepository.findConferenceById(conferenceId);
        conference.setStatus("OPEN_RESULT");
        conferenceRepository.save(conference);
        return "open success";

    }

    public Set<Paper> reviewResults(String username, Long conferenceId){
        return paperRepository.findPapersByConferenceIdAndUserId(conferenceId,userRepository.findByUsername(username).getId());
    }


    public String download(Long paperId, HttpServletResponse response) throws IOException {
        Paper paper = paperRepository.findPaperById(paperId);
        String filename = paper.getTitle() + ".pdf";
        String filepath = paper.getUrl();

        // 如果文件名不为空，则进行下载
        File file = new File(filepath);
        // 如果文件存在，则进行下载
        if (file.exists()) {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 实现文件下载
            byte[] buffer = new byte[1024];

            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis);){
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

            } catch (IOException e) {
                return "failed";
            }
            return "successfully";
        }
        return "";

    }

    public Paper paperAuthority(String username, Long paperId){
        User user = userRepository.findByUsername(username);
        Paper paper = paperRepository.findPaperById(paperId);
        if(paper==null){
            return null;
        }
        if(paper.getUserId().equals(user.getId())){
            paper.setUrl(AUTHOR);
        }
        UserAndConference userConference = userConferenceRepository.findUserConferenceByUserIDAndConferenceID(user.getId(), paper.getConferenceId());
        Set<Paper> papers = userConference.getPapers();
        for(Paper paperOfReview : papers){
            if(paperOfReview.getId().equals(paperId)) {
                paper.setUrl(PC_MEMBER);
                break;
            }
        }

        Topic topic = new Topic();
        Conference conference = conferenceRepository.findConferenceById(paper.getConferenceId());

        if(conference.getOwner().equals(username)&&paper.getUrl().equals(PC_MEMBER))
            paper.setUrl("CP");
        else if(conference.getOwner().equals(username))
            paper.setUrl(CHAIR);

        topic.setTopic(conference.getTopics());
        topic.setTag(conference.getStatus());
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        Set<ReviewResult> reviewResults = paper.getReviewResults();
        for(ReviewResult reviewResult : reviewResults){
            if(reviewResult.getIdOfPcMember().equals(user.getId())){
                paper.setStatus(-1);
                break;
            }
        }
        paper.setnTopics(topics);
        paper.setType(conference.getStatus());
        return paper;
    }



    //判断某会议中paper的review result是否都处于某状态/到某阶段了
    public boolean checkReviewResultStatus(Long conferenceId, int status){
        Set<Paper> papers = paperRepository.findPapersByConferenceId(conferenceId);
        for(Paper paper : papers){
            //第二阶段中，只需要对有rebuttal的paper进行确认即可
            if(status==2&&paper.getRebuttal()==null)
                continue;
            Set<ReviewResult> reviewResults = paper.getReviewResults();
            for(ReviewResult reviewResult : reviewResults)
                if(reviewResult.getConfirm()!=status)
                    return false;
        }
        return true;
    }

}
