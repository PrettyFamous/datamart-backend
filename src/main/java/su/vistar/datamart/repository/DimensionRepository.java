package su.vistar.datamart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.vistar.datamart.entity.Dimension;
import su.vistar.datamart.entity.User;
import java.util.List;

@Repository
public interface DimensionRepository extends CrudRepository<Dimension, Long> {

    Boolean existsByName(String name);

    Dimension findDimensionByNameAndUser(String name, User user);

    @Query("Select d from Dimension d where d.name like %:name%")
    Page<Dimension> findAllByName(@Param("name") String name, Pageable pageable);

    Page<Dimension> findAll(Pageable pageable);

    @Query(value = "SELECT nextval('system_name_seq');", nativeQuery = true)
    Long getUniqueVal();

    @Query(value = "select column_name from information_schema.columns where table_name=:systemName",
            nativeQuery = true, countQuery = "select count(distinct column_name) from information_schema.columns")
    List<String> getSystemTableColumns(@Param("systemName") String systemName);
}
