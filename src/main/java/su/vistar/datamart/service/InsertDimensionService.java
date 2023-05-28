package su.vistar.datamart.service;

public interface InsertDimensionService {
    void createAndFillDimension(String dimensionName, String fillData);
}
