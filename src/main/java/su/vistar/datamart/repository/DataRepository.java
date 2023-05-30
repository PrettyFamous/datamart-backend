package su.vistar.datamart.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<String[]> GetData(String tableName, String columns) {
        Query query = entityManager.createNativeQuery("SELECT " + columns + " FROM " + tableName + ";");
        List<Object[]> queryResult = query.getResultList();

        List<String[]> stringList = new ArrayList<>();
        queryResult.forEach((item) -> {
            String[] row = new String[item.length];

            for (int i=0; i<item.length; i++) {
                row[i] = String.valueOf(item[i]);
            }

            stringList.add(row);
        });

        return stringList;
    }

    // FIXME только для одной колонки в dimensions!!
    public String[] GetValues(String tableName, String columns) {
        Query query = entityManager.createNativeQuery("SELECT " + columns + " FROM " + tableName + ";");
        List<Object> queryResult = query.getResultList();

        String[] values = new String[queryResult.size()];
        for (int i=0; i< queryResult.size(); i++){
            values[i] = String.valueOf(queryResult.get(i));
        }

        return values;
    }

    public int GetRowIdByTableAndValue(String tableName, String value, String columnName) {
        Query query = entityManager.createNativeQuery("SELECT id_" + tableName + " FROM " + tableName + " WHERE " + columnName + "=\'" + value +"\';");

        return  (int)query.getSingleResult();
    }

    public String GetValueByTableAndId(String tableName, Long id, String columnName) {
        Query query = entityManager.createNativeQuery("SELECT " + columnName + " FROM " + tableName + " WHERE id_" + tableName + "=" + id + ";");

        return (String)query.getSingleResult();
    }

}
