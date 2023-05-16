package jp.co.axa.apidemo.api.v1.domain;

import jp.co.axa.apidemo.entities.Role;

import java.util.*;

public class UserDto {

    private UUID userId;
    private String email;
    private String username;
    private String password;
    private Set<Role> roles = new HashSet<>();
    private Boolean isNotLocked;


    public UserDto() {
    }

    public UserDto(UUID userId, String email, String username, String password, Set<Role> roles, Boolean isNotLocked) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.isNotLocked = isNotLocked;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Boolean getNotLocked() {
        return isNotLocked;
    }

    public void setNotLocked(Boolean notLocked) {
        isNotLocked = notLocked;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isNotLocked=" + isNotLocked +
                '}';
    }
}
