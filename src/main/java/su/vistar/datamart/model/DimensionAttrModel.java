package su.vistar.datamart.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DimensionAttrModel {
    @JsonProperty(value="id")
    private Long id;

    @JsonProperty(value="name")
    private String name;

    @JsonProperty(value="typeId")
    private Long typeId;

    @JsonProperty(value="dimensionId")
    private Long dimensionId;
}
