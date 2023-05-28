package su.vistar.datamart.serviceimpl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import su.vistar.datamart.entity.Dimension;
import com.ibm.icu.text.Transliterator;
import su.vistar.datamart.entity.DimensionAttr;
import su.vistar.datamart.entity.Type;
import su.vistar.datamart.entity.User;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.repository.DimensionAttrRepository;
import su.vistar.datamart.repository.DimensionRepository;
import su.vistar.datamart.repository.TypeRepository;
import su.vistar.datamart.service.InsertDimensionService;
import su.vistar.datamart.service.UserService;

import java.util.Calendar;

import static com.fasterxml.jackson.core.io.NumberInput.parseLong;

@Service
@AllArgsConstructor
public class InsertDimensionServiceImpl implements InsertDimensionService {

    DimensionRepository dimensionRepository;
    DimensionAttrRepository dimensionAttrRepository;
    TypeRepository typeRepository;
    UserService userService;
    private final Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createAndFillDimension(String dimensionName, String fillData) {
        User user = userService.getUserById(parseLong("1"));

        String systemName = toLatinTrans.transliterate(dimensionName.replaceAll(" ", "_"));
        systemName += "_" + dimensionRepository.getUniqueVal();


        // Creating table
        String query = "CREATE TABLE " + systemName + "(" +
                "id_" + systemName + " integer PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY" + ");";
        jdbcTemplate.execute(query);

        Dimension dimension = new Dimension(
                dimensionName,
                systemName,
                Calendar.getInstance(),
                user
        );
        dimensionRepository.save(dimension);

        String[] content = fillData.split("\n");

        // Creating attributes
        String[] attributes = content[0].split(",");
        for (int i=0; i< attributes.length; i++) {
            Type type = typeRepository.findByName("text").orElseThrow(() -> new ResourceNotFoundException(
                    "Resource \"Type\" with name=text does not exist."));
            query = "ALTER TABLE " + systemName
                    + " ADD COLUMN " + attributes[i] + " text;";
            jdbcTemplate.execute(query);


            DimensionAttr dimensionAttr = new DimensionAttr(
                    attributes[i],
                    type,
                    dimension);
            dimensionAttrRepository.save(dimensionAttr);
        }

        // Filling with data
        for (int i=1; i<content.length; i++) {
            content[i].replaceAll("\"","\'");

            query = "INSERT INTO " + systemName + " (" + content[0]
                    + ") VALUES (" + content[i] + ");";

            jdbcTemplate.execute(query);
        }
    }
}
