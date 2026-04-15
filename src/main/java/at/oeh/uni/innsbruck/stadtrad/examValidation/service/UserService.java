package at.oeh.uni.innsbruck.stadtrad.examValidation.service;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.UserDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.User;
import at.oeh.uni.innsbruck.stadtrad.examValidation.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User update(UserDto user) throws UsernameNotFoundException {
        User currentUser = userRepository.findByUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            currentUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }

        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        String authorities = String.join("::", user.getAuthorities());
        currentUser.setAuthorities(authorities);

        return saveUser(currentUser);
    }

    public User createUser(UserDto user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(user.getEmail());
        newUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        String authorities = String.join("::", user.getAuthorities());
        newUser.setAuthorities(authorities);
        return saveUser(newUser);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
