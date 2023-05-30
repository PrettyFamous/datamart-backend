package su.vistar.datamart.service;

import org.springframework.data.domain.Page;
import su.vistar.datamart.entity.Fact;
import su.vistar.datamart.model.FactInfoModel;
import su.vistar.datamart.model.FactModel;
import su.vistar.datamart.model.PageDTO;

public interface FactService {
    Fact getFactById(Long id);

    Page<Fact> getFacts(String name, PageDTO pageDTO);

    FactInfoModel getFactInfoById(Long id);

    Fact addFact(FactModel factModel);

    Fact updateFact(FactModel factModel);

    void deleteById(Long id);
}
