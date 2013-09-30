package eu.speedbadminton.pyramid.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Yoann Moranville
 * Date: 25/06/2013
 *
 * @author Yoann Moranville
 */

@Document(collection = "player")
public class Player {
    @Id
    private String id;

    private String name;

    private long pyramidPosition;

    private String email;

    private String password; //todo: TO BE ENCRYPTED

    private Role role;

    private Gender gender;

    private List<Match> matches;

    public Player() {}

    public Player(String name, String email, String password, Gender gender) {
        super();
        this.name = name;
        this.email = email;
        this.password = encrypt(password);
        this.gender = gender;
    }

    //todo
    private String encrypt(String password) {
        return password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Match> getMatches() {
        if(matches == null)
            return new ArrayList<Match>();
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public long getPyramidPosition() {
        return pyramidPosition;
    }

    public void setPyramidPosition(long pyramidPosition) {
        this.pyramidPosition = pyramidPosition;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public enum Role {
        ADMIN, NONE
    }

    public enum Gender {
        MALE, FEMALE
    }

    @Override
    public String toString() {
        return "Player [id=" + id + ", name=" + name + ", pyramidPosition=" + pyramidPosition + ", email=" + email + ", role=" + role.toString() + ", gender=" + gender.toString() + "]";
    }
}
