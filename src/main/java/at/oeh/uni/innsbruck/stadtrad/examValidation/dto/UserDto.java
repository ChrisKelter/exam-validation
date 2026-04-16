package at.oeh.uni.innsbruck.stadtrad.examValidation.dto;

import at.oeh.uni.innsbruck.stadtrad.examValidation.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> authorities;

    public UserDto() {}

    private UserDto(String username, String firstName, String lastName, String email, String authorities) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.authorities = Arrays.stream(authorities.split("::")).collect(Collectors.toList());
    }

    public static UserDto fromModel(User user) {
        UserDto dto = new UserDto(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAuthoritiesAsString());

        return dto;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
