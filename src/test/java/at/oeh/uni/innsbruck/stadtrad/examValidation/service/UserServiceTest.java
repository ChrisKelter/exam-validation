package at.oeh.uni.innsbruck.stadtrad.examValidation.service;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.UserDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private  UserService userService;


    @Test
    public void initialization_test() {
        User admin = (User) this.userService.loadUserByUsername("admin");
        User user = (User) this.userService.loadUserByUsername("user");
        User validation = (User) this.userService.loadUserByUsername("validation");

        assertEquals("admin", admin.getUsername());
        assertEquals("Admin", admin.getFirstName());
        assertEquals("ISTRATOR", admin.getLastName());
        assertEquals("admin@oeh.at", admin.getEmail());
        assertAuthorities(List.of("admin", "user"), admin.getAuthorities());

        assertEquals("user", user.getUsername());
        assertEquals("user", user.getFirstName());
        assertEquals("The 1", user.getLastName());
        assertEquals("user@oeh.at", user.getEmail());
        assertAuthorities(List.of("user"), user.getAuthorities());

        assertEquals("validation", validation.getUsername());
        assertEquals("validation", validation.getFirstName());
        assertEquals("The 2", validation.getLastName());
        assertEquals("validation@oeh.at", validation.getEmail());
        assertAuthorities(List.of("validation"), validation.getAuthorities());
    }

    public void assertAuthorities(List<String> expected, Collection<? extends GrantedAuthority> actual) {
        List<String> actualAuthorities = actual.stream().map(GrantedAuthority::getAuthority).toList();
        assertEquals(expected.stream().sorted().toList(), actualAuthorities.stream().sorted().toList());
    }

    @Test
    public void testLoadUser_notFound() {
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("john"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testGetAllUsers_authorized() {
        List<User> users = userService.getAllUsers();
        assertEquals(3, users.size());
    }

    @Test
    @WithMockUser(authorities = "user")
    void testGetAllUsers_forbidden() {

        assertThrows(AccessDeniedException.class,
                () -> userService.getAllUsers());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testUpdate_withPassword() {
        UserDto dto = new UserDto();
        dto.setUsername("user");
        dto.setPassword("newpass");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@oeh.at");
        dto.setAuthorities(List.of("user", "validation"));

        User updated = userService.update(dto);

        assertNotNull(updated.getPassword()); // encoded
        assertTrue(new BCryptPasswordEncoder().matches("newpass", updated.getPassword()));
        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals("john.doe@oeh.at", updated.getEmail());
        assertAuthorities(List.of("user", "validation"), updated.getAuthorities());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testUpdate_withoutPassword() {
        UserDto dto = new UserDto();
        dto.setUsername("user");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@oeh.at");
        dto.setAuthorities(List.of("user", "validation"));

        User updated = userService.update(dto);

        assertNotNull(updated.getPassword()); // encoded
        assertTrue(new BCryptPasswordEncoder().matches("passwd", updated.getPassword()));
        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals("john.doe@oeh.at", updated.getEmail());
        assertAuthorities(List.of("user", "validation"), updated.getAuthorities());
    }

    @Test
    @WithMockUser(authorities = "user")
    void testUpdate_unauthorized() {
        UserDto dto = new UserDto();
        dto.setUsername("user");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@oeh.at");
        dto.setAuthorities(List.of("user", "validation"));

        assertThrows(AccessDeniedException.class,
                () -> userService.update(dto));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testCreateUser_success() {
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setEmail("john@oeh.at");
        dto.setPassword("pass");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAuthorities(List.of("user"));

        User created = userService.createUser(dto);

        assertEquals("john", created.getUsername());
        assertNotNull(created.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("pass", created.getPassword()));
        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertEquals("john@oeh.at", created.getEmail());
        assertAuthorities(List.of("user"), created.getAuthorities());
        assertEquals(4, userService.getAllUsers().size());
        assertNotNull(userService.loadUserByUsername("john"));
    }

    @Test
    @WithMockUser(authorities = "user")
    void testCreateUser_unauthorized() {
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setEmail("john@oeh.at");
        dto.setPassword("pass");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAuthorities(List.of("user"));

        assertThrows(AccessDeniedException.class,
                () -> userService.createUser(dto));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testCreateUser_duplicateUsername() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(dto));
    }
}
