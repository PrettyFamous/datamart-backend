package su.vistar.datamart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import su.vistar.datamart.entity.Fact;

@Repository
public interface FactRepository extends CrudRepository<Fact, Long> {
        boolean existsByName(String name);

        @Query(value = "SELECT nextval('system_name_seq');", nativeQuery = true)
        Long getUniqueVal();

        Fact findBySystemName(String systemName);

        @Query("Select f from Fact f where f.name like %:name%")
        Page<Fact> findAllByName(@Param("name") String name, Pageable pageable);

        Page<Fact> findAll(Pageable pageable);

        @Query(value = "Select system_name from fact_attr where id_fact=:factId and id_type=3", nativeQuery = true)
        String getSystemNameOfValueColumn(@Param("factId") Long factId);
}
