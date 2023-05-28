package su.vistar.datamart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name="fact_attr")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class FactAttr {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_fact_attr")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "system_name")
    private String systemName;


    @NonNull
    @JoinColumn(name = "id_type")
    @ManyToOne
    private Type type;

    @NonNull
    @JoinColumn(name = "id_fact")
    @ManyToOne
    private Fact fact;
}
