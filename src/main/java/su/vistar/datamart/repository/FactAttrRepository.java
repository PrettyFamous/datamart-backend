package su.vistar.datamart.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.vistar.datamart.entity.Fact;
import su.vistar.datamart.entity.FactAttr;
import su.vistar.datamart.entity.Type;

import java.util.Optional;

@Repository
public interface FactAttrRepository extends CrudRepository<FactAttr, Long> {
     boolean existsByName(String name);

    boolean existsByNameAndFact(String name, Optional<Fact> fact);

    FactAttr[] getAllByFact_Id(Long fact_id);

    FactAttr findByNameAndFact(String name, Fact fact);
}
