package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

/**
 * @author LBW
 */
@Entity
public class User implements UserDetails {

    private static final long serialVersionUID = -6140085056226164016L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String email;
    private String organization;
    private String region;
    private String fullname;
    private String authority;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Conference> conferences = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "user")
    @JsonManagedReference
    private Set<Message> messages = new HashSet<>();


    public User() {
    }

    public User(String username, String password, String email, String organization, String region, String fullname, String authority) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.organization = organization;
        this.region = region;
        this.fullname = fullname;
        this.authority = authority;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Authority> set = new HashSet<>();
        set.add(new Authority(authority));
        return set;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority.getAuthority();
    }

    public String getAuthority() {
        return authority;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Set<Conference> getConferences() {
        return conferences;
    }

    public void setConferences(Set<Conference> conferences) {
        this.conferences = conferences;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", organization='" + organization + '\'' +
                ", region='" + region + '\'' +
                ", fullname=" + fullname + '\'' +
                ", authority=" + authority +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(o != null)
            return this.toString().equals(o.toString());
        else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
