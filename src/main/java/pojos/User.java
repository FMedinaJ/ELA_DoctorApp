package pojos;

import java.util.Objects;

public class User {
    private Integer id;
    private String username;
    private String passwordEncripted;
    private Role role;

    public User(Integer id, String username, String passwordEncripted, Role role) {
        this.id = id;
        this.username = username;
        this.passwordEncripted = passwordEncripted;
        this.role = role;
    }

    public User(Integer id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPasswordEncripted() {
        return passwordEncripted;
    }
    public void setPasswordEncripted(String passwordEncripted) {
        this.passwordEncripted = passwordEncripted;

    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(passwordEncripted, user.passwordEncripted) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passwordEncripted, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwordEncripted='" + passwordEncripted + '\'' +
                ", role=" + role +
                '}';
    }
}
