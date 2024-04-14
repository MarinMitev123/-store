package bg.tu.sofia.store.web;

import bg.tu.sofia.store.exception.InvalidEntityIdException;
import bg.tu.sofia.store.model.User;
import bg.tu.sofia.store.model.enums.Role;
import bg.tu.sofia.store.security.IsAdmin;
import bg.tu.sofia.store.security.IsAuthenticated;
import bg.tu.sofia.store.service.FilesStorageService;
import bg.tu.sofia.store.service.UserService;
import bg.tu.sofia.store.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FilesStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = {"", "/"})
    @IsAdmin
    public Set<User> getUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    @IsAuthenticated
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping(value = "/current-user")
    @IsAuthenticated
    public User getCurrentUser(Authentication authentication) {
        User from = (User) authentication.getPrincipal();
        log.info("User:{}", from);
        User toReturn = userService.getUserByUsername(from.getUsername());
        log.info("User2:{}", from);
        return toReturn;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @IsAdmin
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User created = userService.createUser(user);
        URI location =
                MvcUriComponentsBuilder.fromMethodName(UserController.class, "addUser", User.class)
                        .pathSegment("{id}")
                        .buildAndExpand(created.getId())
                        .toUri();
        log.info("User created: {}", location);
        return ResponseEntity.created(location).body(created);
    }

    @RequestMapping(
            value = "{id}",
            method = RequestMethod.PUT,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @IsAuthenticated
    public ResponseEntity<User> updateImage(
            @PathVariable("id") Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "user") String userJson,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        User user = JsonUtil.mapObject(userJson, User.class);
        if (!id.equals(user.getId())) {
            throw new InvalidEntityIdException(
                    String.format(
                            "Entity ID='%s' is different from URL resource ID='%s'",
                            user.getId(), id));
        }
        if (!currentUser.getRole().equals(Role.ADMIN)
                && !currentUser.getId().equals(user.getId())) {
            throw new InvalidEntityIdException(
                    String.format(
                            "User with ID='%s' cannot edit User='%s'",
                            currentUser.getId(), user.getId()));
        }
        if (file != null) {
            String fileName = fileStorageService.save(file);
            String url =
                    MvcUriComponentsBuilder.fromMethodName(
                                    FilesController.class, "getFile", fileName)
                            .build()
                            .toString();
            user.setImageUrl(url);
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            User userById = userService.getUserById(user.getId());
            user.setPassword(userById.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User updateUser = userService.updateUser(user);
        log.info("User updated: {}", updateUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("{id}")
    @IsAuthenticated
    public User remove(@PathVariable("id") Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format(
                            "User with ID='%s' cannot delete User='%s'", currentUser.getId(), id));
        }
        return userService.deleteUser(id);
    }
}