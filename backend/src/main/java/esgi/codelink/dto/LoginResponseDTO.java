package esgi.codelink.dto;

public class LoginResponseDTO { // la reponse si le login c'est bien passer

    private long userId;

    private String status;

    private String token;

    private int nbFollowers;

    private int nbFollowing;

    private String lastName;
    private String firstName;

    private int nbPosts;


    public LoginResponseDTO(String status) {
        this.status = status;
    }

    public LoginResponseDTO(long userId, String status, String token, int nbFollowers, int nbFollowing, int nbPosts, String firstName,String lastName) {
        this.userId = userId;
        this.status = status;
        this.token = token;
        this.nbFollowers = nbFollowers;
        this.nbFollowing = nbFollowing;
        this.nbPosts = nbPosts;
        this.lastName=lastName;
        this.firstName=firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getFirstName(){
        return firstName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }
    public void setLastName(String lastName){ this.lastName=lastName;}
    public void setFirstName(String firstName){this.firstName=firstName;
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

        public int getNbPosts() {
            return nbPosts;
        }

        public void setNbPosts(int nbPosts) {
            this.nbPosts = nbPosts;
        }
    }