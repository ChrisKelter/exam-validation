package at.oeh.uni.innsbruck.stadtrad.examValidation.controller;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.UserDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.User;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authenticated")
    public UserDto getAuthenticatedUser(Principal principal) {
        String username = principal.getName();
        return UserDto.fromModel((User) this.userService.loadUserByUsername(username));
    }

    @GetMapping()
    public UserDto getUser(@RequestParam String username) {
        try{
            return UserDto.fromModel((User) this.userService.loadUserByUsername(username));
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        return this.userService.getAllUsers()
                .stream().map(UserDto::fromModel).collect(Collectors.toList());
    }

    @PutMapping()
    public UserDto updateUser(@RequestBody UserDto user) {
        return UserDto.fromModel(this.userService.update(user));
    }

    @PostMapping()
    public UserDto createUser(@RequestBody UserDto user) {
        return UserDto.fromModel(this.userService.createUser(user));
    }
}
