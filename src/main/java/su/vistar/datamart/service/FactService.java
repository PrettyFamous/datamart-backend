package su.vistar.datamart.service;


import su.vistar.datamart.entity.Fact;
import su.vistar.datamart.model.DimensionInfoModel;
import su.vistar.datamart.model.FactInfoModel;
import su.vistar.datamart.model.FactModel;

public interface FactService {
    Fact getFactById(Long id);

    Iterable<Fact> getFacts(String name);

    FactInfoModel getFactInfoById(Long id);

    Fact addFact(FactModel factModel);

    Fact updateFact(FactModel factModel);

    void deleteById(Long id);
}
