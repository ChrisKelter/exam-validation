package at.oeh.uni.innsbruck.stadtrad.examValidation.controller;

import at.oeh.uni.innsbruck.stadtrad.examValidation.dto.UserDto;
import at.oeh.uni.innsbruck.stadtrad.examValidation.model.User;
import at.oeh.uni.innsbruck.stadtrad.examValidation.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public UserDto getAuthenticatedUser(Principal principal) {
        String username = principal.getName();
        return UserDto.fromModel((User) this.userService.loadUserByUsername(username));
    }
}
