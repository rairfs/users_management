package br.ufs.user_manager.controllers;

import br.ufs.user_manager.dtos.CreateUserDTO;
import br.ufs.user_manager.dtos.LoginRequest;
import br.ufs.user_manager.dtos.LoginResponse;
import br.ufs.user_manager.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
