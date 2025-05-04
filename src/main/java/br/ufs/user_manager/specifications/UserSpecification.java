package br.ufs.user_manager.specifications;

import br.ufs.user_manager.entities.User;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filter(String name, String email) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%" + name.toLowerCase() + "%"));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("email"), email));
            }

            Expression<Object> maxDate = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.greaterThan(root.get("updatedAt"), root.get("createdAt")), root.get("updatedAt"))
                    .otherwise(root.get("createdAt"));

            query.orderBy(criteriaBuilder.desc(maxDate));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
