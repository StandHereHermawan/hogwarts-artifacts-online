package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;

public class MyUserPrincipal implements UserDetails {

    private HogwartsUser hogwartsUser;

    public MyUserPrincipal(HogwartsUser hogwartsUser) {
        this
                .hogwartsUser = hogwartsUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert a user's roles from space-delimited String to a
        // list of "SimpleGrantedAuthority" object.
        // E.g., John's roles are stored in a String like "admin user moderator",
        // we need to convert it to a List.
        // Before conversion, we need to add this "ROLE_" prefix to each role name.
        return Arrays.stream(StringUtils
                        .tokenizeToStringArray(this.hogwartsUser.getRoles(), " "))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Override
    public String getPassword() {
        return this.getHogwartsUser()
                .getPassword();
    }

    @Override
    public String getUsername() {
        return this.hogwartsUser
                .getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Line 35 is default code.
        // return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Line 42 is default code.
        // return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Line 49 is default code.
        // return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Line 55 is default code.
        // return UserDetails.super.isEnabled();
        return this.hogwartsUser
                .isEnabled();
    }

    public HogwartsUser getHogwartsUser() {
        return this
                .hogwartsUser;
    }
}
