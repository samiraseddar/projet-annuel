package esgi.codelink.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @JsonIgnore
    @Column(unique = true)
    private String mail;

    @JsonIgnore
    private String password;


    private int nbFollowers;

    private int nbFollowing;

    private int nbPosts;


    @OneToMany(mappedBy = "user")// un user c'est une clé etrangére pour un token
    private Set<Token> tokens;

    public User() { }

    public User(String mail, String password) {
        this.mail = mail;
        this.password = password;
        this.tokens = new HashSet<>();
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNbFollowers() {
        return nbFollowers;
    }

    public void setNbFollowers(int nbFollowers) {
        this.nbFollowers = nbFollowers;
    }

    public int getNbFollowing() {
        return nbFollowing;
    }

    public void setNbFollowing(int nbFollowing) {
        this.nbFollowing = nbFollowing;
    }

    public void incrementFollowers() {
        this.nbFollowers++;
    }

    public void incrementFollowing() {
        this.nbFollowing++;
    }

    public void decrementFollowers() {
        this.nbFollowers--;
    }

    public void decrementFollowing() {
        this.nbFollowing--;
    }

    public int getNbPosts() {
        return nbPosts;
    }


    public void setNbPosts(int nbReviews) {
        this.nbPosts = nbReviews;
    }
            }
