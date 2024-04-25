package esgi.codelink.entity;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {// c'est pour représentée les details de l'etulisateur paraport a les infos dautontification .

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

   @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
//le role on return une liste vide .


    public User getUser() {
        return user;
    }
    public String getMail() {
        return user.getMail();
    }

    public long getUserId() {
        return user.getUserId();
    }

    public void setUserId(long userId) {
        user.setUserId(userId);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getMail();
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

}

