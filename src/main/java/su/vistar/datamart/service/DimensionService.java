package su.vistar.datamart.service;

import org.springframework.data.domain.Page;
import su.vistar.datamart.entity.Dimension;
import su.vistar.datamart.model.DimensionInfoModel;
import su.vistar.datamart.model.DimensionModel;
import su.vistar.datamart.model.PageDTO;

public interface DimensionService {
    Dimension getDimensionById(Long id);

    DimensionInfoModel getDimensionInfoById(Long id);

    String[] getDimensionValuesById(Long id);

    Page<Dimension> getDimensions(String name, PageDTO pageDTO);

    Dimension addDimension(DimensionModel dimensionModel);

    void deleteById(Long id);
}
