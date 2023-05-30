package su.vistar.datamart.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.vistar.datamart.entity.Dimension;
import su.vistar.datamart.entity.DimensionAttr;

import java.util.Optional;

@Repository
public interface DimensionAttrRepository extends CrudRepository<DimensionAttr, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndDimension(String name, Optional<Dimension> dimension);

    DimensionAttr findByDimension(Dimension dimension);
}
