package su.vistar.datamart.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.vistar.datamart.entity.Type;

import java.util.Optional;

@Repository
public interface TypeRepository extends CrudRepository<Type, Long> {
    Optional<Type> findByName(String name);
    boolean existsByName(String name);
}
