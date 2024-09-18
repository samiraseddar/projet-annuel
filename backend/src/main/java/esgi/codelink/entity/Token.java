package esgi.codelink.entity;

import jakarta.persistence.*;

@Entity
public class Token { // la table de token pour representer les token dans user

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)//pour mettre la condition a la colonne
    private String token;

    private boolean expired;

    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY) //plusieur token pour un utulisateur
    @JoinColumn(name = "user_id") // c'est pour evité la table de jointure
    private User user;

    public Token(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public Token() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
