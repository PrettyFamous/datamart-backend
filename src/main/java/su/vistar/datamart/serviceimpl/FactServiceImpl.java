package su.vistar.datamart.serviceimpl;

import com.ibm.icu.text.Transliterator;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import su.vistar.datamart.entity.*;
import su.vistar.datamart.exception.ResourceAlreadyExistsException;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.model.FactInfoModel;
import su.vistar.datamart.model.FactModel;
import su.vistar.datamart.model.PageDTO;
import su.vistar.datamart.repository.*;
import su.vistar.datamart.service.DimensionService;
import su.vistar.datamart.service.FactService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@AllArgsConstructor
public class FactServiceImpl implements FactService {
    private final FactRepository factRepository;
    private final FactAttrRepository factAttrRepository;
    private final UserRepository userRepository;
    private final DimensionService dimensionService;
    private final DimensionRepository dimensionRepository;
    private final Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");
    private final JdbcTemplate jdbcTemplate;
    private final DataRepository dataRepository;
    private final DimensionAttrRepository dimensionAttrRepository;

    @Override
    public Fact getFactById(Long id) {
        return factRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Fact\" with id=" + id + " does not exist"
                ));
    }

    @Override
    public Page<Fact> getFacts(String name, PageDTO pageDTO) {
        if (name == "") {
            return factRepository.findAll(pageDTO.getPageable());
        } else {
            return factRepository.findAllByName(name, pageDTO.getPageable());
        }
    }

    public FactInfoModel getFactInfoById(Long id) {
        Fact fact = factRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Fact\" with id=" + id + " does not exist"
                ));

        FactAttr[] factAttrs = factAttrRepository.getAllByFact_Id(id);

        String[] attrNames = new String[factAttrs.length];
        for (int i=0; i< factAttrs.length; i++) {
            attrNames[i] = factAttrs[i].getName();
        }

        List<String[]> attrValues = new ArrayList<>();
        for (int i=0; i< factAttrs.length; i++) {
            if (!factAttrs[i].getType().getName().equals("dimension")) {
                attrValues.add(new String[0]);
            } else {
                String[] dimValues = dimensionService.getDimensionValuesById(
                        dimensionRepository.findDimensionByNameAndUser( factAttrs[i].getName(), fact.getUser() ).getId()
                );

                attrValues.add(dimValues);
            }
        }

        List<String[]> rowData =  new ArrayList<>();
        rowData.add(dataRepository.GetValues(fact.getSystemName(), "id_" + fact.getSystemName()));
        for (int i=0; i < factAttrs.length; i++) {
            if (factAttrs[i].getType().getId() != 4) {
                String[] columnData = dataRepository.GetValues(fact.getSystemName(), factAttrs[i].getSystemName());

                rowData.add(columnData);
            } else {
                String[] columnData = dataRepository.GetValues(fact.getSystemName(), "id_" + factAttrs[i].getSystemName());
                for (int j=0; j<columnData.length; j++) {
                    String columnNameAtDimensionTable = dimensionAttrRepository.findByDimension(dimensionRepository.findDimensionByNameAndUser( factAttrs[i].getName(), fact.getUser() )).getName();
                    columnData[j] = dataRepository.GetValueByTableAndId(factAttrs[i].getSystemName(), Long.parseLong(columnData[j]), columnNameAtDimensionTable);
                }
                rowData.add(columnData);
            }
        }

        List<String[]> data = new ArrayList<>();
        for (int i=0; i < rowData.get(0).length; i++) {
            String[] row = new String[rowData.size()];
            for (int j=0; j < rowData.size(); j++) {
                row[j] = rowData.get(j)[i];
            }

            data.add(row);
        }


        FactInfoModel factInfoModel = new FactInfoModel(
                fact.getId(),
                fact.getName(),
                attrNames,
                attrValues,
                data
        );

        return factInfoModel;
    };

    @Override
    public Fact addFact(FactModel factModel) {
        User user = userRepository
                .findById(factModel.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Namespace\" with id=" + factModel.getUserId() + " does not exist."));

        if (factRepository.existsByName( factModel.getName() )) {
            throw new ResourceAlreadyExistsException("Fact with such name already exists.");
        }

        if (Character.isDigit(factModel.getName().charAt(0))) {
            throw new IllegalArgumentException("Could not create table with digit at first position");
        }

        String systemName = toLatinTrans.transliterate(factModel.getName()).replaceAll(" ", "_");
        systemName += "_" + factRepository.getUniqueVal();

        String query = "CREATE TABLE " + systemName + "(" +
                "id_" + systemName + " integer PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY" + ");";
        jdbcTemplate.execute(query);

        Fact fact = new Fact(
                factModel.getName(),
                systemName,
                Calendar.getInstance(),
                user
        );
        factRepository.save(fact);

        return fact;
    }

    @Override
    public Fact updateFact(Long factId, String dimensionName, Boolean isFact, String newValue, Long rowId) {
        Fact fact = factRepository.findById(factId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Resource \"Fact\" with id=" + factId + " does not exist."));


        String nameOfIdColumn = "id_" + fact.getSystemName();

        if (isFact) {
            String systemColumnName = factRepository.getSystemNameOfValueColumn(factId);

            dataRepository.updateValue(fact.getSystemName(), nameOfIdColumn, rowId, systemColumnName, Double.valueOf(newValue));
        } else {
            Dimension dimension = dimensionRepository.findDimensionByNameAndUser(dimensionName, fact.getUser());
            DimensionAttr dimensionAttr = dimensionAttrRepository.findByDimension(dimension);
            FactAttr factAttr = factAttrRepository.findByNameAndFact(dimensionName, fact);
            int newDimensionRowId = dataRepository.GetRowIdByTableAndValue(dimension.getSystemName(), newValue, dimensionAttr.getName());
            String nameOfDimensionColumnInFactTable = "id_" + factAttr.getSystemName();

            dataRepository.updateDimension(fact.getSystemName(), nameOfIdColumn, rowId, nameOfDimensionColumnInFactTable, Long.valueOf(newDimensionRowId));
        }

        return fact;
    }

    @Override
    public void deleteById(Long id) {
        try {
            jdbcTemplate.execute("DROP TABLE " + factRepository.findById(id).get().getSystemName() +" CASCADE;");
            jdbcTemplate.execute("DELETE FROM fact_attr WHERE id_fact=" + id + ";");

            factRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Fact with id " + id + " does not exist.", e);
        }
    }
}
