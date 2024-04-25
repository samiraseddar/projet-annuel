package esgi.codelink.dto;

public class LoginResponseDTO { // la reponse si le login c'est bien passer

    private long userId;

    private String status;

    private String token;

    private int nbFollowers;

    private int nbFollowing;

    private int nbPosts;


    public LoginResponseDTO(String status) {
        this.status = status;
    }

    public LoginResponseDTO(long userId, String status, String token, int nbFollowers, int nbFollowing, int nbPosts) {
        this.userId = userId;
        this.status = status;
        this.token = token;
        this.nbFollowers = nbFollowers;
        this.nbFollowing = nbFollowing;
        this.nbPosts = nbPosts;
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