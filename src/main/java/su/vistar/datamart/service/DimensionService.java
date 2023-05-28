package su.vistar.datamart.service;

import su.vistar.datamart.entity.Dimension;
import su.vistar.datamart.model.DimensionInfoModel;
import su.vistar.datamart.model.DimensionModel;

public interface DimensionService {
    Dimension getDimensionById(Long id);

    DimensionInfoModel getDimensionInfoById(Long id);

    String[] getDimensionValuesById(Long id);

    Iterable<Dimension> getDimensions(String name);

    Dimension addDimension(DimensionModel dimensionModel);

    void deleteById(Long id);
}
