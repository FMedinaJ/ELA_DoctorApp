package pojos;

import java.util.Objects;

public class User {
    private Integer id;
    private String email;
    private byte[] passwordEncripted;
    private Role role;

    public User(Integer id, String username, byte[] passwordEncripted, Role role) {
        this.id = id;
        this.email = username;
        this.passwordEncripted = passwordEncripted;
        this.role = role;
    }

    public User(String username,byte[] passwordEncripted, Role role) {
        this.passwordEncripted = passwordEncripted;
        this.email = username;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public byte[] getPasswordEncripted() {
        return passwordEncripted;
    }
    public void setPasswordEncripted(byte[] passwordEncripted) {
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
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(passwordEncripted, user.passwordEncripted) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, passwordEncripted, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + email + '\'' +
                ", passwordEncripted='" + passwordEncripted + '\'' +
                ", role=" + role +
                '}';
    }
}
