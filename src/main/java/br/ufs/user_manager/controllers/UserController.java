package br.ufs.user_manager.controllers;

import br.ufs.user_manager.dtos.CreateUserDTO;
import br.ufs.user_manager.dtos.LoginRequest;
import br.ufs.user_manager.dtos.LoginResponse;
import br.ufs.user_manager.dtos.UserDTO;
import br.ufs.user_manager.services.UserService;
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
    public ResponseEntity<Page<UserDTO>> getUsers(Pageable pageable) {
        Page<UserDTO> userDTOS = userService.findAll(pageable);
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
}
