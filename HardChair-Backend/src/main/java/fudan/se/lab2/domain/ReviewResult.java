package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class ReviewResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    int score;
    String comment;
    String confidence;
    Long idOfPcMember;
    //0为未确认，1为第一阶段确认，2为第二阶段确认
    int confirm;

    boolean isPcMember;

    @ManyToOne()
    @JoinColumn
    @JsonIgnore
    private Paper paper;

    public ReviewResult(){
        // no codes here
    }

    public ReviewResult(int score, String comment, String confidence, Long idOfPcMember){
        this.score = score;
        this.comment = comment;
        this.confidence = confidence;
        this.idOfPcMember = idOfPcMember;
        this.confirm = 0;
        this.isPcMember = false;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Long getIdOfPcMember() {
        return idOfPcMember;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setIdOfPcMember(Long idOfPcMember) {
        this.idOfPcMember = idOfPcMember;
    }

    public boolean isPcMember() {
        return isPcMember;
    }

    public void setPcMember(boolean pcMember) {
        isPcMember = pcMember;
    }
}
