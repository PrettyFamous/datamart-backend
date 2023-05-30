package su.vistar.datamart.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Calendar;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DimensionModel {
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
