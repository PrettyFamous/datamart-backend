package su.vistar.datamart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name="dimension_attr")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class DimensionAttr {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_dimension_attr")
    private Long id;
    @NonNull
    @Column(name = "name")
    private String name;


    @NonNull
    @JoinColumn(name = "id_type")
    @ManyToOne
    private Type type;

    @NonNull
    @JoinColumn(name = "id_dimension")
    @ManyToOne
    private Dimension dimension;
}
