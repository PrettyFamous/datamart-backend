package su.vistar.datamart.service;

import su.vistar.datamart.entity.Type;
import su.vistar.datamart.model.TypeModel;

public interface TypeService {
    Type getTypeById(Long id);

    Iterable<Type> getTypes();

    Type addType(TypeModel typeModel);

    Type updateType(TypeModel typeModel);

    void deleteById(Long id);
}
