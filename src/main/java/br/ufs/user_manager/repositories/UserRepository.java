package br.ufs.user_manager.repositories;

import br.ufs.user_manager.entities.User;
import br.ufs.user_manager.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, Status status);

    default Page<User> findAllByStatus(Status status, Specification<User> spec, Pageable pageable) {
        Specification<User> finalSpec = Specification.where(spec);

        finalSpec = finalSpec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status));

        return findAll(finalSpec, pageable);
    }
}
