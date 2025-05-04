package br.ufs.user_manager.repositories;

import br.ufs.user_manager.entities.User;
import br.ufs.user_manager.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Page<User> findAllByStatus(Status status, Pageable pageable);
}
