package br.ufs.user_manager.config;

import br.ufs.user_manager.entities.Address;
import br.ufs.user_manager.entities.Role;
import br.ufs.user_manager.entities.User;
import br.ufs.user_manager.enums.RoleType;
import br.ufs.user_manager.enums.Status;
import br.ufs.user_manager.repositories.RoleRepository;
import br.ufs.user_manager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${admin-email}")
    private String adminEmail;

    @Value("${admin-password}")
    private String adminPassword;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Optional<Role> roleOptional = roleRepository.findByName(RoleType.ADMIN.name());

        if (roleOptional.isEmpty()) {
            throw new RuntimeException("There is no admin role");
        }

        Optional<User> userOptional = userRepository.findByEmail(adminEmail);

        userOptional.ifPresentOrElse(
                (user) -> {
                    System.out.println("Admin user already exists");
                },
                () -> {
                    User user = new User();
                    user.setEmail(adminEmail);
                    user.setName("Admin");
                    user.setPassword(bCryptPasswordEncoder.encode(adminPassword));
                    user.setStatus(Status.ACTIVE);

                    Address address = new Address();
                    address.setUser(user);
                    address.setStreetName("Rua A");
                    address.setNumber("1");
                    address.setDistrict("Any");
                    address.setPostalCode("12345");
                    address.setCity("São Paulo");
                    address.setState("SP");
                    address.setComplement("Any");

                    List<Address> addresses = List.of(address);

                    user.setAddresses(addresses);
                    user.setRoles(Set.of(roleOptional.get()));

                    userRepository.save(user);
                }
        );
    }
}
