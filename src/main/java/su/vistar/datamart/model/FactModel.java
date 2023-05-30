package su.vistar.datamart.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.Calendar;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class FactModel {
    @JsonProperty(value="id")
    private Long id;

    @JsonProperty(value="name")
    private String name;

    @JsonProperty(value="namespaceId")
    private Long userId;

    @JsonProperty(value="createdDate")
    private Calendar createdDate;

    @JsonProperty(value="isDimension")
    private boolean isDimension;
}
