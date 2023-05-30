package su.vistar.datamart.model;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class PageDTO {
    private int pageNumber = 0;
    private int pageSize = 18;
    private Sort.Direction sortDirection = Sort.Direction.DESC;
    private String sortBy = "id";

    public Pageable getPageable() {
        return PageRequest.of(
                this.pageNumber,
                this.pageSize,
                this.sortDirection,
                this.sortBy);
    }
}
