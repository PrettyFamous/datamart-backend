package su.vistar.datamart.serviceimpl;

import com.ibm.icu.text.Transliterator;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.vistar.datamart.entity.*;
import su.vistar.datamart.exception.ResourceAlreadyExistsException;
import su.vistar.datamart.exception.ResourceNotFoundException;
import su.vistar.datamart.repository.*;
import su.vistar.datamart.service.InsertFactsService;
import su.vistar.datamart.service.UserService;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.fasterxml.jackson.core.io.NumberInput.parseLong;

@Service
@AllArgsConstructor
public class InsertFactsServiceImpl implements InsertFactsService {
    private final UserService userService;
    private final Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");
    private final JdbcTemplate jdbcTemplate;
    private final DataRepository dataRepository;
    private final TypeRepository typeRepository;
    private final FactRepository factRepository;
    private final DimensionRepository dimensionRepository;
    private final FactAttrRepository factAttrRepository;
    private final DimensionAttrRepository dimensionAttrRepository;


    private class Pair<F, S> {
        public final F first;
        public final S second;

        protected Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

    @Override
    public void createAndFillFacts(MultipartFile templateFile, String templateFileName, MultipartFile dataFile, String dataFileName) {
        try {
            File data = multipartToFile(dataFile, dataFileName);
            File template = multipartToFile(templateFile, templateFileName);

            FileInputStream dataFileInputStream = new FileInputStream(data);
            FileInputStream templateFileInputStream = new FileInputStream(template);

            Sheet dataSheet = new XSSFWorkbook(dataFileInputStream).getSheetAt(0);
            Sheet templateSheet = new XSSFWorkbook(templateFileInputStream).getSheetAt(0);


            // Preparing variables
            List<Pair<String, String>> globalDimensions = getListOfGlobalDimensions(templateSheet, dataSheet);


            // Iterating through excel
            boolean isTableCreated = false;
            String factTableName = "";
            for (int i = templateSheet.getFirstRowNum(); i <= templateSheet.getLastRowNum(); i++) {
                Row row = templateSheet.getRow(i);

                for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                    Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    if (cell.getStringCellValue().length() > 0 && cell.getStringCellValue().charAt(0) == '$') {
                        List<Pair<String, String>> localDimensions = new ArrayList<>();

                        // Собираем дименшены вверх по вертикали
                        for (int v = i; v >= 0; v--) {
                            Cell verticalCell = templateSheet.getRow(v).getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            if (verticalCell.getStringCellValue().length() > 0
                                    && verticalCell.getStringCellValue().charAt(0) == '&') {
                                localDimensions.add(
                                        new Pair<>(
                                                verticalCell.getStringCellValue().substring(1),
                                                dataSheet.getRow(v).getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()
                                        )
                                );
                            }
                        }

                        // Собираем дименшены влево по горизонтали
                        for (int h = cellIndex; h >= 0; h--) {
                            Cell horizontalCell = templateSheet.getRow(i).getCell(h, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            if (horizontalCell.getStringCellValue().length() > 0 && horizontalCell.getStringCellValue().charAt(0) == '&') {
                                localDimensions.add(
                                        new Pair<>(
                                                horizontalCell.getStringCellValue().substring(1),
                                                dataSheet.getRow(i).getCell(h, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()
                                        )
                                );
                            }
                        }

                        String factName = templateSheet.getRow(i).getCell(cellIndex).getStringCellValue().substring(1);
                        double factValue = dataSheet.getRow(i).getCell(cellIndex).getNumericCellValue();

                        List<Pair<String, String>> allDimensions = Stream.concat(localDimensions.stream(), globalDimensions.stream()).collect(Collectors.toList());

                        if (!isTableCreated) {
                            factTableName = createFactTable(dataFileName, factName, allDimensions);
                            isTableCreated = true;
                        }

                        addRowIntoFactTable(factTableName, factName, factValue, allDimensions);
                    }
                }
            }
        } catch (Exception e ) {
            System.err.println(e);
        }
    }

    private void addRowIntoFactTable(String factTableName, String factName, double factValue, List<Pair<String, String>> dimensionList) {
        // 1. Создать список колонок в таблице
        User user = userService.getUserById(parseLong("1"));

        String columns = "";
        String values = "";

        for (int i=0; i < dimensionList.size(); i++) {
            String dimensionSystemName = dimensionRepository.findDimensionByNameAndUser(dimensionList.get(i).first, user).getSystemName().toLowerCase();
            columns += "id_" + dimensionSystemName + ", ";

            String dimensionAttrName = dimensionAttrRepository.findByDimension(
                    dimensionRepository.findDimensionByNameAndUser(dimensionList.get(i).first, user)
            ).getName();
            int id_dimension = dataRepository.GetRowIdByTableAndValue(dimensionSystemName, dimensionList.get(i).second, dimensionAttrName);
            values += id_dimension + ", ";
        }

        columns += factAttrRepository.findByNameAndFact(factName, factRepository.findBySystemName(factTableName)).getSystemName().toLowerCase();
        values += factValue;


        // 2. Запихнуть данные в том же порядке
        String query = "INSERT INTO " + factTableName + " (" + columns
                + ") VALUES (" + values + ");";

        jdbcTemplate.execute(query);
    }

    private String createFactTable(String tableName, String factName, List<Pair<String, String>> dimensionList) {
        User user = userService.getUserById(parseLong("1"));

        if (factRepository.existsByName( tableName )) {
            throw new ResourceAlreadyExistsException("Fact with such name already exists.");
        }

        if (Character.isDigit(tableName.charAt(0))) {
            throw new IllegalArgumentException("Could not create table with digit at first position");
        }

        String systemName = toLatinTrans.transliterate(tableName).replaceAll(" ", "_").toLowerCase();
        systemName += "_" + factRepository.getUniqueVal();

        String query = "CREATE TABLE " + systemName + "(" +
                "id_" + systemName + " integer PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY" + ");";
        jdbcTemplate.execute(query);

        Fact fact = new Fact(
                tableName,
                systemName,
                Calendar.getInstance(),
                user
        );
        factRepository.save(fact);

        // Создаём колонку для данных
        String attrSystemName = toLatinTrans.transliterate(factName).replaceAll(" ", "_").toLowerCase();
        attrSystemName += "_" + factRepository.getUniqueVal();

        query = "ALTER TABLE " + systemName + " ADD COLUMN " + attrSystemName + " real;";
        jdbcTemplate.execute(query);

        Type type = typeRepository
                .findByName("real")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource \"Type\" with name=\"real\" does not exist."));


        FactAttr factAttr = new FactAttr(
                factName,
                attrSystemName,
                type,
                fact
        );
        factAttrRepository.save(factAttr);


        // Создаём колонки с ID дименшенов
        for (int i=0; i< dimensionList.size(); i++) {
            String dimensionSystemName = dimensionRepository.findDimensionByNameAndUser(dimensionList.get(i).first, user).getSystemName().toLowerCase();
            query = "ALTER TABLE " + systemName + " ADD COLUMN id_" + dimensionSystemName + " integer;";
            jdbcTemplate.execute(query);

            query = "ALTER TABLE " + systemName +
                    " ADD CONSTRAINT fk_" + dimensionSystemName +
                    " FOREIGN KEY (id_" + dimensionSystemName + ") REFERENCES " +
                    dimensionSystemName + "(id_" + dimensionSystemName + ")  ON DELETE CASCADE;";
            jdbcTemplate.execute(query);

            type = typeRepository
                    .findByName("dimension")
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Resource \"Type\" with name=\"real\" does not exist."));

            factAttr = new FactAttr(
                    dimensionList.get(i).first,
                    dimensionSystemName,
                    type,
                    fact
            );
            factAttrRepository.save(factAttr);
        }

        return systemName;
    }

    private List<Pair<String, String>> getListOfGlobalDimensions(Sheet templateSheet, Sheet dataSheet) {
        List<Pair<String, String>> globalDimensions = new ArrayList<>(); // Название Dimension – его поле Name
        for (int i = templateSheet.getFirstRowNum() + 1; i <= templateSheet.getLastRowNum(); i++) {
            Row row = templateSheet.getRow(i);

            for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                if (cell.getStringCellValue().length() > 0 && cell.getStringCellValue().charAt(0) == '!') {
                    globalDimensions.add(
                            new Pair<String, String>(
                                    cell.getStringCellValue().substring(1),
                                    dataSheet.getRow(i).getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()
                            )
                    );
                }
            }
        }

        return globalDimensions;
    }

    private File multipartToFile(MultipartFile multipart, String fileName) {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        try {
            multipart.transferTo(convFile);
        } catch (Exception e ) {
            System.err.println(e);
        }

        return convFile;
    }


    private static void printCellValue(Cell cell) {
        CellType cellType = cell.getCellType().equals(CellType.FORMULA)
                ? cell.getCachedFormulaResultType() : cell.getCellType();
        if (cellType.equals(CellType.STRING)) {
            System.out.print(cell.getStringCellValue() + " | ");
        }
        if (cellType.equals(CellType.NUMERIC)) {
            if (DateUtil.isCellDateFormatted(cell)) {
                System.out.print(cell.getDateCellValue() + " | ");
            } else {
                System.out.print(cell.getNumericCellValue() + " | ");
            }
        }
        if (cellType.equals(CellType.BOOLEAN)) {
            System.out.print(cell.getBooleanCellValue() + " | ");
        }
    }
}
