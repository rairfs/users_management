package br.ufs.user_manager.controllers;

import br.ufs.user_manager.dtos.*;
import br.ufs.user_manager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse authenticate = userService.authenticate(loginRequest);
        return ResponseEntity.ok(authenticate);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody CreateUserDTO dto) {
        userService.register(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            Pageable pageable
    ) {
        Page<UserDTO> userDTOS = userService.findAll(name, email, pageable);
        return ResponseEntity.ok(userDTOS);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getLoggedUser() {
        UserDTO currentUserInfo = userService.findCurrentUserInfo();
        return ResponseEntity.ok(currentUserInfo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserDTO userDTO, @PathVariable Long id) {
        UserDTO updated = userService.update(userDTO, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/{email}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> addAdminAccess(@PathVariable String email) {
        userService.addAdminRole(email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/noadmin/{email}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> removeAdminAccess(@PathVariable String email) {
        userService.removeAdminRole(email);
        return ResponseEntity.noContent().build();
    }
}
