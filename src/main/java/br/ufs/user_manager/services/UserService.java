package br.ufs.user_manager.services;

import br.ufs.user_manager.clients.ViaCepClient;
import br.ufs.user_manager.dtos.*;
import br.ufs.user_manager.entities.Address;
import br.ufs.user_manager.entities.Role;
import br.ufs.user_manager.entities.User;
import br.ufs.user_manager.enums.RoleType;
import br.ufs.user_manager.enums.Status;
import br.ufs.user_manager.repositories.RoleRepository;
import br.ufs.user_manager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ViaCepClient viaCepClient;

    @Value("${expiration-time}")
    private Long expiresIn;

    public UserService(JwtEncoder jwtEncoder, UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, ViaCepClient viaCepClient) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.viaCepClient = viaCepClient;
    }

    @Transactional
    public LoginResponse authenticate(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.email());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Instant now = Instant.now();

        String scopes = user.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("UserManager")
                .subject(user.get().getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, expiresIn);
    }

    public void register(CreateUserDTO dto) {
        Optional<Role> roleOptional = roleRepository.findByName(RoleType.BASIC.name());

        if (roleOptional.isEmpty()) {
            throw new RuntimeException("Role not found");
        }

        Optional<User> userOptional = userRepository.findByEmail(dto.email());

        if (userOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        List<Address> addresses = new ArrayList<>();

        for (AddressDTO addressDTO : dto.enderecos()) {
            CepResponse addressFound = viaCepClient.getCep(addressDTO.cep());

            Address address = new Address();
            address.setStreetName(addressFound.logradouro());
            address.setCity(addressFound.localidade());
            address.setState(addressFound.uf());
            address.setPostalCode(addressFound.cep());
            address.setDistrict(addressFound.bairro());
            address.setComplement(addressDTO.complemento());

            addresses.add(address);
        }

        User user = new User();
        user.setName(dto.nome());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.senha()));
        user.setAddresses(addresses);
        user.setRoles(Set.of(roleOptional.get()));
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);
    }
}
