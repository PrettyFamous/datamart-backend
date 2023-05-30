package su.vistar.datamart.service;

import su.vistar.datamart.entity.FactAttr;
import su.vistar.datamart.model.FactAttrModel;

public interface FactAttrService {
    FactAttr getFactAttrById(Long id);

    Iterable<FactAttr> getFactAttrs();

    FactAttr addFactAttr(FactAttrModel factAttrModel);

    FactAttr updateFactAttr(FactAttrModel factAttrModel);

    void deleteById(Long id);
}
