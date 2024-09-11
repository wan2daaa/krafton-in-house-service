package me.wane.adldap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.wane.adldap.dto.CreateUserRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public String addUser(
            @RequestBody CreateUserRequest request
    ) {
        userService.saveUser(request);
        return "User added successfully!";
    }

    @PatchMapping("/{cn}/password")
    public Boolean updatePassword(
            @PathVariable String cn,
            @RequestParam String newPassword
    ) {
        return userService.updatePassword(cn, newPassword);
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{cn}")
    public User getUser(@PathVariable String cn) {
        return userService.findUserByCN(cn);
    }

    @PatchMapping("/{cn}")
    public String updateUser(
            @PathVariable String cn,
            @RequestParam String newDisplayName,
            @RequestParam String newEmail
    ) {
        userService.updateUser(cn, newDisplayName, newEmail);
        return "User updated successfully!";
    }

    @GetMapping("/auth")
    public boolean verifyUser(
            @RequestParam String cn,
            @RequestParam String password
    ) {
        log.info("cn: {}, password: {}", cn, password);
        return userService.authenticate(cn, password);
    }
}
