package su.vistar.datamart.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DimensionInfoModel {
    @JsonProperty(value="id")
    private Long id;

    @JsonProperty(value="name")
    private String name;

    @JsonProperty(value="columnNames")
    private String[] columnNames;

    @JsonProperty(value="rows")
    private List<String[]> rows;
}
