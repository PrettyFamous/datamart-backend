package su.vistar.datamart.service;

import su.vistar.datamart.entity.DimensionAttr;
import su.vistar.datamart.model.DimensionAttrModel;

public interface DimensionAttrService {
    DimensionAttr getDimensionAttrById(Long id);

    Iterable<DimensionAttr> getDimensionAttrs();

    DimensionAttr addDimensionAttr(DimensionAttrModel dimensionAttrModel);

    DimensionAttr updateDimensionAttr(DimensionAttrModel dimensionAttrModel);

    void deleteById(Long id);
}
