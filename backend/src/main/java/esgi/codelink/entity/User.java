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
    private String lastName;
    private String firstName;

    @OneToMany(mappedBy = "user")// un user c'est une clé etrangére pour un token
    private Set<Token> tokens;

    //@OneToMany(mappedBy = "follower")
    //private Set<Follow> followings = new HashSet<>();

   // @OneToMany(mappedBy = "following")
    //private Set<Follow> followers = new HashSet<>();
    public User() { }

    public User(String mail, String password,String lastName,String firstName) {
        this.mail = mail;
        this.password = password;
        this.lastName=lastName;
        this.firstName=firstName;
        this.tokens = new HashSet<>();
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
    public String getLastName(){
        return lastName;
    }
    public String getFirstName(){
        return firstName;
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

    public void incrementNbPosts() {
        this.nbPosts++;
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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", mail='" + mail + '\'' +
                ", nbFollowers=" + nbFollowers +
                ", nbFollowing=" + nbFollowing +
                ", nbPosts=" + nbPosts +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}



