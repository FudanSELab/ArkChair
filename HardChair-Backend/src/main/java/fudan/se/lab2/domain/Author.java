package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String name;
    String organization;
    String region;
    String email;
    int orderOfAuthor;

    @ManyToOne()
    @JoinColumn
    @JsonIgnore
    private Paper paper;

    public Author(){}

    public Author(String name, String organization, String region, String email){
        this.name = name;
        this.organization = organization;
        this.region = region;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getOrderOfAuthor() {
        return orderOfAuthor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setOrderOfAuthor(int orderOfAuthor) {
        this.orderOfAuthor = orderOfAuthor;
    }
}
