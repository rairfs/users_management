package br.ufs.user_manager.services;

import br.ufs.user_manager.clients.ViaCepClient;
import br.ufs.user_manager.dtos.*;
import br.ufs.user_manager.entities.Address;
import br.ufs.user_manager.entities.Role;
import br.ufs.user_manager.entities.User;
import br.ufs.user_manager.enums.RoleType;
import br.ufs.user_manager.enums.Status;
import br.ufs.user_manager.exceptions.EntityAlreadyExistsException;
import br.ufs.user_manager.exceptions.EntityNotFoundException;
import br.ufs.user_manager.repositories.RoleRepository;
import br.ufs.user_manager.repositories.UserRepository;
import br.ufs.user_manager.specifications.UserSpecification;
import br.ufs.user_manager.utils.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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

    @Transactional
    public void register(CreateUserDTO dto) {
        Role role = getRole(RoleType.BASIC);

        Optional<User> userOptional = userRepository.findByEmailAndStatus(dto.email(), Status.ACTIVE);

        if (userOptional.isPresent()) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(Set.of(role));
        user.setStatus(Status.ACTIVE);

        List<Address> addresses = new ArrayList<>();

        for (AddressCreationDTO addressCreationDTO : dto.addresses()) {
            Address address = getAddressFromService(addressCreationDTO, user);
            addresses.add(address);
        }

        user.setAddresses(addresses);

        userRepository.save(user);
    }

    public Page<UserDTO> findAll(String name, String email, Pageable pageable) {
        Specification<User> spec = UserSpecification.filter(name, email);
        Page<User> pagedUser = userRepository.findAllByStatus(Status.ACTIVE, spec, pageable);

        List<UserDTO> userDTOS = pagedUser.stream().map(p -> new UserDTO(
                p.getName(),
                p.getEmail(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getAddresses().stream().map(a -> new AddressResponseDTO(
                        a.getStreetName(),
                        a.getNumber(),
                        a.getComplement(),
                        a.getDistrict(),
                        a.getCity(),
                        a.getState(),
                        a.getPostalCode()
                )).toList()
        )).toList();
        return new PageImpl<>(userDTOS, pageable, userDTOS.size());
    }

    public UserDTO findCurrentUserInfo() {
        Long userID = CurrentUserUtils.getCurrentUserID();
        return getUserDTO(userID);
    }

    public UserDTO findById(Long id) {
        if (!CurrentUserUtils.isCurrentUserAdmin() && !CurrentUserUtils.getCurrentUserID().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }
        return getUserDTO(id);
    }

    @Transactional
    public UserDTO update(UpdateUserDTO dto, Long id) {
        validateAccess(id);

        User userFound = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (dto.name() != null)
            if (!dto.name().trim().isEmpty()) userFound.setName(dto.name());

        if (dto.email() != null)
            if (!dto.email().trim().isEmpty()) userFound.setEmail(dto.email());

        if (dto.password() != null)
            if (!dto.password().trim().isEmpty()) userFound.setPassword(passwordEncoder.encode(dto.password()));


        if (dto.addresses() != null && !dto.addresses().isEmpty()) {
            List<Address> addressUpdate = new ArrayList<>();
            for (AddressCreationDTO addressCreationDTO : dto.addresses()) {
                Address address = getAddressFromService(addressCreationDTO, userFound);
                addressUpdate.add(address);
            }
            userFound.setAddresses(addressUpdate);
        }

        User saved = userRepository.save(userFound);

        return new UserDTO(
                saved.getName(),
                saved.getEmail(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getAddresses().stream().map(address -> new AddressResponseDTO(
                        address.getStreetName(),
                        address.getNumber(),
                        address.getComplement(),
                        address.getDistrict(),
                        address.getCity(),
                        address.getState(),
                        address.getPostalCode()
                )).toList()
        );


    }

    @Transactional
    public void deleteById(Long id) {
        validateAccess(id);

        User userFound = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userFound.setStatus(Status.INACTIVE);
        userRepository.save(userFound);
    }

    @Transactional
    public void addAdminRole(String email) {

        Role adminRole = getRole(RoleType.ADMIN);

        Optional<User> userOptional = userRepository.findByEmailAndStatus(email, Status.ACTIVE);

        if (userOptional.isEmpty()) throw new EntityNotFoundException("User not found");
        if (userOptional.get().getRoles().contains(adminRole)) {
            throw new EntityAlreadyExistsException("User already has admin role");
        }

        User user = userOptional.get();
        Set<Role> currentRoles = user.getRoles();
        currentRoles.add(adminRole);
        user.setRoles(currentRoles);
        userRepository.save(user);

    }

    @Transactional
    public void removeAdminRole(String email) {
        Role adminRole = getRole(RoleType.ADMIN);

        Optional<User> userOptional = userRepository.findByEmailAndStatus(email, Status.ACTIVE);

        if (userOptional.isEmpty()) throw new EntityNotFoundException("User not found");
        if (!userOptional.get().getRoles().contains(adminRole)) {
            throw new EntityNotFoundException("User does not have admin role");
        }

        User user = userOptional.get();
        Set<Role> currentRoles = user.getRoles();
        currentRoles.remove(adminRole);
        user.setRoles(currentRoles);
    }

    private Role getRole(RoleType roleType) {
        Optional<Role> roleOptional = roleRepository.findByName(roleType.toString());
        if (roleOptional.isEmpty()) throw new EntityNotFoundException("Role not found");
        return roleOptional.get();
    }

    private void validateAccess(Long id) {
        boolean isAdmin = CurrentUserUtils.isCurrentUserAdmin();
        boolean isSelf = CurrentUserUtils.getCurrentUserID().equals(id);
        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private Address getAddressFromService(AddressCreationDTO addressCreationDTO, User user) {
        CepResponse cepResponse = viaCepClient.getCep(addressCreationDTO.postalCode());
        Address address = new Address();
        address.setStreetName(cepResponse.logradouro());
        address.setCity(cepResponse.localidade());
        address.setState(cepResponse.uf());
        address.setPostalCode(cepResponse.cep());
        address.setDistrict(cepResponse.bairro());
        address.setComplement(addressCreationDTO.complement());
        address.setNumber(addressCreationDTO.number());
        address.setUser(user);
        return address;
    }

    private UserDTO getUserDTO(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return new UserDTO(
                user.get().getName(),
                user.get().getEmail(),
                user.get().getCreatedAt(),
                user.get().getUpdatedAt(),
                user.get().getAddresses().stream().map(a -> new AddressResponseDTO(
                        a.getStreetName(),
                        a.getNumber(),
                        a.getComplement(),
                        a.getDistrict(),
                        a.getCity(),
                        a.getState(),
                        a.getPostalCode()
                )).toList()
        );
    }


}
