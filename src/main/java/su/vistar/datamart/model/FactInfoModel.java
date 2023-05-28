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
public class FactInfoModel {
    @JsonProperty(value="factId")
    private Long factId;

    @JsonProperty(value="factName")
    private String factName;

    @JsonProperty(value="factAttrs")
    private String[] factAttrs;

    @JsonProperty(value="attrValues")
    private List<String[]> attrValues;

    @JsonProperty(value="data")
    private List<String[]> data;
}
