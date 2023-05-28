package su.vistar.datamart.serviceimpl;

import com.ibm.icu.text.Transliterator;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import su.vistar.datamart.entity.Fact;
import su.vistar.datamart.entity.FactAttr;
import su.vistar.datamart.entity.User;
import su.vistar.datamart.exception.ResourceAlreadyExistsException;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.model.FactInfoModel;
import su.vistar.datamart.model.FactModel;
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
    public Iterable<Fact> getFacts(String name) {
        if (name == "") {
            return factRepository.findAll();
        } else {
            return factRepository.findAllByName(name);
        }
    }

    public FactInfoModel getFactInfoById(Long id) {
        Fact fact = factRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Fact\" with id=" + id + " does not exist"
                ));

        FactAttr[] factAttrs = factAttrRepository.getAllByFact_Id(id);


        // Формируем массив с названиями атрибутов
        String[] attrNames = new String[factAttrs.length];
        for (int i=0; i< factAttrs.length; i++) {
            attrNames[i] = factAttrs[i].getName();
        }


        // Формируем список с массивами значений атрибутов
        List<String[]> attrValues = new ArrayList<>();
        for (int i=0; i< factAttrs.length; i++) {
            if (!factAttrs[i].getType().getName().equals("dimension")) {
                attrValues.add(new String[0]);
            } else {
                String[] dimValues = dimensionService.getDimensionValuesById(
                        dimensionRepository.findDimensionByNameAndUser( factAttrs[i].getName(), fact.getUser() ).getId()
                );

                // FIXME Поправить, если нужны составные дименшены
                attrValues.add(dimValues);
            }
        }


        // Формируем Матрицу с данными ПО КОЛОНКАМ
        List<String[]> rowData =  new ArrayList<>();
        for (int i=0; i < factAttrs.length; i++) {
            if (factAttrs[i].getType().getId() != 4) { // Если не дименшен
                String[] columnData = dataRepository.GetValues(fact.getSystemName(), factAttrs[i].getSystemName()); // Берём данные из самой таблицы

                rowData.add(columnData);
            } else { // Если дименшен, то надо подставить значение вместо ID
                String[] columnData = dataRepository.GetValues(fact.getSystemName(), "id_" + factAttrs[i].getSystemName());
                for (int j=0; j<columnData.length; j++) {
                    // Берём ID дименшена
                    String columnNameAtDimensionTable = dimensionAttrRepository.findByDimension(dimensionRepository.findDimensionByNameAndUser( factAttrs[i].getName(), fact.getUser() )).getName();
                    columnData[j] = dataRepository.GetValueByTableAndId(factAttrs[i].getSystemName(), Long.parseLong(columnData[j]), columnNameAtDimensionTable);
                }
                rowData.add(columnData);
            }
        }

        // Транспонируем данные
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

    // TODO Сделать нативный запрос на пересоздание таблицы
    @Override
    public Fact updateFact(FactModel factModel) {
        User user = userRepository
                .findById(factModel.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Namespace\" with id=" + factModel.getUserId() + " does not exist."));

        Fact fact = factRepository.findById(factModel.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Fact\" with name=" + factModel.getName() + " does not exist."));

        String systemName = toLatinTrans.transliterate(factModel.getName()).replaceAll(" ", "_");
        systemName += "_" + factRepository.getUniqueVal();

        // 1. Удалить старую таблицу
        // 2. Создатиь таблицу с нвоым именем
        // 3. Добавить в неё все атрибуты, которые были в старой табилице, для чего:
        //      1) из репозитория достать все dimensionAttr, у которых idDimension равен id текущего Dimension
        //      2) в цикле сделать "ALTER... ADD COLUMN..." для всех найденых атрибутов


        fact.setName(factModel.getName());
        fact.setSystemName(systemName);
        fact.setUser(user);
        factRepository.save(fact);

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
